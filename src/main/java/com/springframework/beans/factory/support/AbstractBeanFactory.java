package com.springframework.beans.factory.support;

import com.springframework.beans.BeanWrapper;
import com.springframework.beans.PropertyEditorRegistrar;
import com.springframework.beans.SimpleTypeConverter;
import com.springframework.beans.TypeConverter;
import com.springframework.beans.config.BeanDefinition;
import com.springframework.beans.factory.BeanFactory;
import com.springframework.beans.factory.BeanFactoryUtils;
import com.springframework.beans.factory.FactoryBean;
import com.springframework.beans.factory.config.*;
import com.springframework.core.DecoratingClassLoader;
import com.springframework.core.NamedThreadLocal;
import com.springframework.core.ResolvableType;
import com.springframework.core.convert.ConversionService;
import com.springframework.util.Assert;
import com.springframework.util.ClassUtils;
import com.springframework.util.ObjectUtils;
import com.springframework.util.StringUtils;
import com.sun.istack.internal.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author 虎哥
 * @Description //TODO
 * 要带着问题去学习,多猜想多验证
 **/
@Slf4j
public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory {
    protected final Log logger = LogFactory.getLog(getClass());
    private final ThreadLocal<Object> prototypesCurrentlyInCreation =
            new NamedThreadLocal<>("Prototype beans currently in creation");
    @Nullable
    private BeanFactory parentBeanFactory;
    private boolean cacheBeanMetadata = true;

    protected AbstractBeanFactory() {
    }

    protected abstract boolean containsBeanDefinition(String beanName);

    /**
     * Map from bean name to merged RootBeanDefinition.
     */
    private final Map<String, RootBeanDefinition> mergedBeanDefinitions = new ConcurrentHashMap<>(256);
    @Nullable
    private ConversionService conversionService;

    public <T> T getBean(String name, @Nullable Class<T> requiredType, @Nullable Object... args)
            throws Exception {
        return doGetBean(name, requiredType, args, false);
    }
    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws Exception {
        return doGetBean(name, requiredType, null, false);
    }

    private final Map<String, Scope> scopes = new LinkedHashMap<>(8);
    @Nullable
    private TypeConverter typeConverter;

    protected String transformedBeanName(String name) {
        return canonicalName(BeanFactoryUtils.transformedBeanName(name));
    }

    protected boolean isPrototypeCurrentlyInCreation(String beanName) {
        Object curVal = this.prototypesCurrentlyInCreation.get();
        return (curVal != null &&
                (curVal.equals(beanName) || (curVal instanceof Set && ((Set<?>) curVal).contains(beanName))));
    }

    @Override
    @Nullable
    public BeanFactory getParentBeanFactory() {
        return this.parentBeanFactory;
    }

    @Nullable
    protected Class<?> predictBeanType(String beanName, RootBeanDefinition mbd, Class<?>... typesToMatch) {
        Class<?> targetType = mbd.getTargetType();
        if (targetType != null) {
            return targetType;
        }
        if (mbd.getFactoryMethodName() != null) {
            return null;
        }
        return resolveBeanClass(mbd, beanName, typesToMatch);
    }

    protected String originalBeanName(String name) {
        String beanName = transformedBeanName(name);
        if (name.startsWith(FACTORY_BEAN_PREFIX)) {
            beanName = FACTORY_BEAN_PREFIX + beanName;
        }
        return beanName;
    }

    @Override
    public boolean isFactoryBean(String name) throws RuntimeException {
        String beanName = transformedBeanName(name);
        Object beanInstance = getSingleton(beanName, false);
        if (beanInstance != null) {
            return (beanInstance instanceof FactoryBean);
        }
        // No singleton instance found -> check bean definition.
        if (!containsBeanDefinition(beanName) && getParentBeanFactory() instanceof ConfigurableBeanFactory) {
            // No bean definition found in this factory -> delegate to parent.
            return ((ConfigurableBeanFactory) getParentBeanFactory()).isFactoryBean(name);
        }
        return isFactoryBean(beanName, getMergedLocalBeanDefinition(beanName));
    }

    protected boolean isFactoryBean(String beanName, RootBeanDefinition mbd) {
        Boolean result = mbd.isFactoryBean;
        if (result == null) {
            Class<?> beanType = predictBeanType(beanName, mbd, FactoryBean.class);
            result = (beanType != null && FactoryBean.class.isAssignableFrom(beanType));
            mbd.isFactoryBean = result;
        }
        return result;
    }

    @Override
    public boolean containsBean(String name) {
        String beanName = transformedBeanName(name);
        if (containsSingleton(beanName) || containsBeanDefinition(beanName)) {
            return (!BeanFactoryUtils.isFactoryDereference(name) || isFactoryBean(name));
        }
        // Not found -> check parent.
        BeanFactory parentBeanFactory = getParentBeanFactory();
        return (parentBeanFactory != null && parentBeanFactory.containsBean(originalBeanName(name)));
    }

    /**
     * Names of beans that have already been created at least once.
     */
    private final Set<String> alreadyCreated = Collections.newSetFromMap(new ConcurrentHashMap<>(256));

    @Override
    public Object getBean(String name) throws Exception {
        return doGetBean(name, null, null, false);
    }

    protected void markBeanAsCreated(String beanName) {
        // 没有创建
        if (!this.alreadyCreated.contains(beanName)) {
            // 加上全局锁
            synchronized (this.mergedBeanDefinitions) {
                // 再次检查一次：DCL 双检查模式
                if (!this.alreadyCreated.contains(beanName)) {
                    // Let the bean definition get re-merged now that we're actually creating
                    // the bean... just in case some of its metadata changed in the meantime.
                    // 从 mergedBeanDefinitions 中删除 beanName，并在下次访问时重新创建它
                    clearMergedBeanDefinition(beanName);
                    // 添加到已创建 bean 集合中
                    // 将这个 beanName 保存在 alreadyCreated 集合中（SetFromMap），在后面的循环依赖检查中会使用到
                    this.alreadyCreated.add(beanName);
                }
            }
        }
    }

    protected void clearMergedBeanDefinition(String beanName) {
        RootBeanDefinition bd = this.mergedBeanDefinitions.get(beanName);
        if (bd != null) {
            bd.stale = true;
        }
    }

    protected RootBeanDefinition getMergedLocalBeanDefinition(String beanName) throws RuntimeException {
        // Quick check on the concurrent map first, with minimal locking.
        // 从 `mergedBeanDefinitions` 缓存中获取合并后的 RootBeanDefinition，存在则直接返回
        RootBeanDefinition mbd = this.mergedBeanDefinitions.get(beanName);
        if (mbd != null && !mbd.stale) {
            return mbd;
        }
        // 获取 BeanDefinition 并转换成，如果存在父子关系则进行合并
        return getMergedBeanDefinition(beanName, getBeanDefinition(beanName));
    }

    protected abstract BeanDefinition getBeanDefinition(String beanName) throws RuntimeException;

    protected RootBeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition bd)
            throws RuntimeException {

        return getMergedBeanDefinition(beanName, bd, null);
    }

    protected RootBeanDefinition getMergedBeanDefinition(
            String beanName, BeanDefinition bd, @Nullable BeanDefinition containingBd)
            throws RuntimeException {
        // 加锁
        synchronized (this.mergedBeanDefinitions) {
            RootBeanDefinition mbd = null;
            RootBeanDefinition previous = null;

            // Check with full lock now in order to enforce the same merged instance.
            if (containingBd == null) {
                mbd = this.mergedBeanDefinitions.get(beanName);
            }

            if (mbd == null || mbd.stale) {
                previous = mbd;
                // 如果没有父类则直接转换成 RootBeanDefinition 对象
                if (bd.getParentName() == null) {
                    // Use copy of given root bean definition.
                    if (bd instanceof RootBeanDefinition) {
                        mbd = ((RootBeanDefinition) bd).cloneBeanDefinition();
                    } else {
                        mbd = new RootBeanDefinition(bd);
                    }
                }
                // 有父类则进行合并
                else {
                    // Child bean definition: needs to be merged with parent.
                    BeanDefinition pbd;
                    try {
                        // 获取父类的对应的 BeanDefinition 对象
                        String parentBeanName = transformedBeanName(bd.getParentName());
                        if (!beanName.equals(parentBeanName)) {
                            pbd = getMergedBeanDefinition(parentBeanName);
                        } else {
                            BeanFactory parent = getParentBeanFactory();
                            if (parent instanceof ConfigurableBeanFactory) {
                                pbd = ((ConfigurableBeanFactory) parent).getMergedBeanDefinition(parentBeanName);
                            } else {
                                throw new RuntimeException(parentBeanName +
                                        "Parent name '" + parentBeanName + "' is equal to bean name '" + beanName +
                                        "': cannot be resolved without a ConfigurableBeanFactory parent");
                            }
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException("Could not resolve parent bean definition '" + bd.getParentName() + "'" + ex);
                    }
                    // Deep copy with overridden values.
                    mbd = new RootBeanDefinition(pbd);
                    // 父子合并
                    mbd.overrideFrom(bd);
                }

                // Set default singleton scope, if not configured before.
                if (!StringUtils.hasLength(mbd.getScope())) {
                    mbd.setScope(SCOPE_SINGLETON);
                }

                // A bean contained in a non-singleton bean cannot be a singleton itself.
                // Let's correct this on the fly here, since this might be the result of
                // parent-child merging for the outer bean, in which case the original inner bean
                // definition will not have inherited the merged outer bean's singleton status.
                if (containingBd != null && !containingBd.isSingleton() && mbd.isSingleton()) {
                    mbd.setScope(containingBd.getScope());
                }

                // Cache the merged bean definition for the time being
                // (it might still get re-merged later on in order to pick up metadata changes)
                if (containingBd == null && isCacheBeanMetadata()) {
                    // 放入缓存中
                    this.mergedBeanDefinitions.put(beanName, mbd);
                }
            }
            if (previous != null) {
                copyRelevantMergedBeanDefinitionCaches(previous, mbd);
            }
            return mbd;
        }
    }

    private void copyRelevantMergedBeanDefinitionCaches(RootBeanDefinition previous, RootBeanDefinition mbd) {
        if (ObjectUtils.nullSafeEquals(mbd.getBeanClassName(), previous.getBeanClassName()) &&
                ObjectUtils.nullSafeEquals(mbd.getFactoryBeanName(), previous.getFactoryBeanName()) &&
                ObjectUtils.nullSafeEquals(mbd.getFactoryMethodName(), previous.getFactoryMethodName())) {
            ResolvableType targetType = mbd.targetType;
            ResolvableType previousTargetType = previous.targetType;
            if (targetType == null || targetType.equals(previousTargetType)) {
                mbd.targetType = previousTargetType;
                mbd.isFactoryBean = previous.isFactoryBean;
                mbd.resolvedTargetType = previous.resolvedTargetType;
                mbd.factoryMethodReturnType = previous.factoryMethodReturnType;
                mbd.factoryMethodToIntrospect = previous.factoryMethodToIntrospect;
            }
        }
    }

    @Override
    public BeanDefinition getMergedBeanDefinition(String name) throws RuntimeException {
        String beanName = transformedBeanName(name);
        // Efficiently check whether bean definition exists in this factory.
        if (!containsBeanDefinition(beanName) && getParentBeanFactory() instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory) getParentBeanFactory()).getMergedBeanDefinition(beanName);
        }
        // Resolve merged bean definition locally.
        return getMergedLocalBeanDefinition(beanName);
    }

    protected void checkMergedBeanDefinition(RootBeanDefinition mbd, String beanName, @Nullable Object[] args)
            throws RuntimeException {

        if (mbd.isAbstract()) {
            throw new RuntimeException(beanName);
        }
    }

    protected void beforePrototypeCreation(String beanName) {
        Object curVal = this.prototypesCurrentlyInCreation.get();
        if (curVal == null) {
            this.prototypesCurrentlyInCreation.set(beanName);
        } else if (curVal instanceof String) {
            Set<String> beanNameSet = new HashSet<>(2);
            beanNameSet.add((String) curVal);
            beanNameSet.add(beanName);
            this.prototypesCurrentlyInCreation.set(beanNameSet);
        } else {
            Set<String> beanNameSet = (Set<String>) curVal;
            beanNameSet.add(beanName);
        }
    }

    protected void afterPrototypeCreation(String beanName) {
        Object curVal = this.prototypesCurrentlyInCreation.get();
        if (curVal instanceof String) {
            this.prototypesCurrentlyInCreation.remove();
        } else if (curVal instanceof Set) {
            Set<String> beanNameSet = (Set<String>) curVal;
            beanNameSet.remove(beanName);
            if (beanNameSet.isEmpty()) {
                this.prototypesCurrentlyInCreation.remove();
            }
        }
    }

//    private final List<BeanPostProcessor> beanPostProcessors = new CopyOnWriteArrayList<>();
//
//    public List<BeanPostProcessor> getBeanPostProcessors() {
//        return this.beanPostProcessors;
//    }

    @Nullable
    protected Class<?> resolveBeanClass(RootBeanDefinition mbd, String beanName, Class<?>... typesToMatch)
            throws RuntimeException {

        try {
            // 有 Class 对象则直接返回
            if (mbd.hasBeanClass()) {
                return mbd.getBeanClass();
            }
            // 否则，调用 `doResolveBeanClass(...)` 方法，加载出一个 Class 对象
            if (System.getSecurityManager() != null) {
                return AccessController.doPrivileged((PrivilegedExceptionAction<Class<?>>)
                        () -> doResolveBeanClass(mbd, typesToMatch), getAccessControlContext());
            } else {
                return doResolveBeanClass(mbd, typesToMatch);
            }
        } catch (PrivilegedActionException | ClassNotFoundException pae) {
            throw new RuntimeException(pae);
        }
    }

    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    @Override
    @Nullable
    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

    @Nullable
    private Class<?> doResolveBeanClass(RootBeanDefinition mbd, Class<?>... typesToMatch)
            throws ClassNotFoundException {
        // 获取 ClassLoader 加载器
        ClassLoader beanClassLoader = getBeanClassLoader();
        ClassLoader dynamicLoader = beanClassLoader;
        boolean freshResolve = false;

        if (!ObjectUtils.isEmpty(typesToMatch)) {
            // When just doing type checks (i.e. not creating an actual instance yet),
            // use the specified temporary class loader (e.g. in a weaving scenario).
            ClassLoader tempClassLoader = getTempClassLoader();
            if (tempClassLoader != null) {
                dynamicLoader = tempClassLoader;
                freshResolve = true;
                if (tempClassLoader instanceof DecoratingClassLoader) {
                    DecoratingClassLoader dcl = (DecoratingClassLoader) tempClassLoader;
                    for (Class<?> typeToMatch : typesToMatch) {
                        dcl.excludeClass(typeToMatch.getName());
                    }
                }
            }
        }
        // 获取 `className`
        String className = mbd.getBeanClassName();
        if (className != null) {
            Object evaluated = evaluateBeanDefinitionString(className, mbd);
            if (!className.equals(evaluated)) {
                // A dynamically resolved expression, supported as of 4.2...
                if (evaluated instanceof Class) {
                    return (Class<?>) evaluated;
                } else if (evaluated instanceof String) {
                    className = (String) evaluated;
                    freshResolve = true;
                } else {
                    throw new IllegalStateException("Invalid class name expression result: " + evaluated);
                }
            }
            // 如果被处理过，则根据这个 `className` 创建一个 Class 对象
            // 创建的 Class 对象不会设置到 `mbd` 中
            if (freshResolve) {
                // When resolving against a temporary class loader, exit early in order
                // to avoid storing the resolved Class in the bean definition.
                if (dynamicLoader != null) {
                    try {
                        return dynamicLoader.loadClass(className);
                    } catch (ClassNotFoundException ex) {
//                        if (log.isTraceEnabled()) {
//                            log.trace("Could not load class [" + className + "] from " + dynamicLoader + ": " + ex);
//                        }
                    }
                }
                return ClassUtils.forName(className, dynamicLoader);
            }
        }

        // Resolve regularly, caching the result in the BeanDefinition...
        // 让 RootBeanDefinition 自己解析出 Class 对象
        return mbd.resolveBeanClass(beanClassLoader);
    }

    @Override
    @Nullable
    public Class<?> getType(String name) throws RuntimeException {
        return getType(name, true);
    }

    protected boolean removeSingletonIfCreatedForTypeCheckOnly(String beanName) {
        if (!this.alreadyCreated.contains(beanName)) {
            removeSingleton(beanName);
            return true;
        } else {
            return false;
        }
    }

//    @Override
//    @Nullable
//    public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
//        String beanName = transformedBeanName(name);
//
//        // Check manually registered singletons.
//        Object beanInstance = getSingleton(beanName, false);
//        if (beanInstance != null && beanInstance.getClass() != NullBean.class) {
//            if (beanInstance instanceof FactoryBean && !BeanFactoryUtils.isFactoryDereference(name)) {
//                return getTypeForFactoryBean((MyFactoryBean<?>) beanInstance);
//            } else {
//                return beanInstance.getClass();
//            }
//        }
//
//        // No singleton instance found -> check bean definition.
//        MyBeanFactory parentBeanFactory = getParentBeanFactory();
//        if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
//            // No bean definition found in this factory -> delegate to parent.
//            return parentBeanFactory.getType(originalBeanName(name));
//        }
//
//        MyRootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
//
//        // Check decorated bean definition, if any: We assume it'll be easier
//        // to determine the decorated bean's type than the proxy's type.
//        MyBeanDefinitionHolder dbd = mbd.getDecoratedDefinition();
//        if (dbd != null && !BeanFactoryUtils.isFactoryDereference(name)) {
//            MyRootBeanDefinition tbd = getMergedBeanDefinition(dbd.getBeanName(), dbd.getBeanDefinition(), mbd);
//            Class<?> targetClass = predictBeanType(dbd.getBeanName(), tbd);
//            if (targetClass != null && !FactoryBean.class.isAssignableFrom(targetClass)) {
//                return targetClass;
//            }
//        }
//
//        Class<?> beanClass = predictBeanType(beanName, mbd);
//
//        // Check bean class whether we're dealing with a FactoryBean.
//        if (beanClass != null && MyFactoryBean.class.isAssignableFrom(beanClass)) {
//            if (!BeanFactoryUtils.isFactoryDereference(name)) {
//                // If it's a FactoryBean, we want to look at what it creates, not at the factory class.
////                return getTypeForFactoryBean(beanName, mbd, allowFactoryBeanInit).resolve();
//            } else {
//                return beanClass;
//            }
//        } else {
//            return (!BeanFactoryUtils.isFactoryDereference(name) ? beanClass : null);
//        }
//    }

    public boolean isBeanNameInUse(String beanName) {
        return isAlias(beanName) || containsLocalBean(beanName) || hasDependentBean(beanName);
    }

    @Override
    public boolean containsLocalBean(String name) {
        String beanName = transformedBeanName(name);
        return ((containsSingleton(beanName) || containsBeanDefinition(beanName)) &&
                (!BeanFactoryUtils.isFactoryDereference(name) || isFactoryBean(beanName)));
    }

    private volatile boolean hasDestructionAwareBeanPostProcessors;

    protected boolean hasDestructionAwareBeanPostProcessors() {
        return this.hasDestructionAwareBeanPostProcessors;
    }

//    protected boolean requiresDestruction(Object bean, MyRootBeanDefinition mbd) {
//        return (bean.getClass() != NullBean.class &&
//                (DisposableBeanAdapter.hasDestroyMethod(bean, mbd) || (hasDestructionAwareBeanPostProcessors() &&
//                        DisposableBeanAdapter.hasApplicableProcessors(bean, getBeanPostProcessors()))));
//    }

//    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, RootBeanDefinition mbd) {
//        AccessControlContext acc = (System.getSecurityManager() != null ? getAccessControlContext() : null);
//        if (!mbd.isPrototype() // 不是原型模式
//                && requiresDestruction(bean, mbd)) { // 有销毁的必要，也就是定义了销毁方法
//            if (mbd.isSingleton()) { // 单例模式
//                // Register a DisposableBean implementation that performs all destruction
//                // work for the given bean: DestructionAwareBeanPostProcessors,
//                // DisposableBean interface, custom destroy method.
//                // 创建一个 DisposableBeanAdapter 对象封装这个 Bean，然后保存在 `disposableBeans` Map 集合中
//                registerDisposableBean(beanName,
//                        new DisposableBeanAdapter(bean, beanName, mbd, getBeanPostProcessors(), acc));
//            } else {// 其他模式
//                // A bean with a custom scope...
//                Scope scope = this.scopes.get(mbd.getScope());
//                if (scope == null) {
//                    throw new IllegalStateException("No Scope registered for scope name '" + mbd.getScope() + "'");
//                }
//                // 创建一个 DisposableBeanAdapter 对象封装这个 Bean，往其他模式的 Scope 对象里面注册
//                scope.registerDestructionCallback(beanName,
//                        new DisposableBeanAdapter(bean, beanName, mbd, getBeanPostProcessors(), acc));
//            }
//        }
//    }

//    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, RootBeanDefinition mbd) {
//        AccessControlContext acc = (System.getSecurityManager() != null ? getAccessControlContext() : null);
//        if (!mbd.isPrototype() // 不是原型模式
//                && requiresDestruction(bean, mbd)) { // 有销毁的必要，也就是定义了销毁方法
//            if (mbd.isSingleton()) { // 单例模式
//                // Register a DisposableBean implementation that performs all destruction
//                // work for the given bean: DestructionAwareBeanPostProcessors,
//                // DisposableBean interface, custom destroy method.
//                // 创建一个 DisposableBeanAdapter 对象封装这个 Bean，然后保存在 `disposableBeans` Map 集合中
//                registerDisposableBean(beanName,
//                        new DisposableBeanAdapter(bean, beanName, mbd, getBeanPostProcessors(), acc));
//            } else {// 其他模式
//                // A bean with a custom scope...
//                Scope scope = this.scopes.get(mbd.getScope());
//                if (scope == null) {
//                    throw new IllegalStateException("No Scope registered for scope name '" + mbd.getScope() + "'");
//                }
//                // 创建一个 DisposableBeanAdapter 对象封装这个 Bean，往其他模式的 Scope 对象里面注册
//                scope.registerDestructionCallback(beanName,
//                        new DisposableBeanAdapter(bean, beanName, mbd, getBeanPostProcessors(), acc));
//            }
//        }
//    }

    protected void initBeanWrapper(BeanWrapper bw) {
        bw.setConversionService(getConversionService());
//        registerCustomEditors(bw);
    }

    private volatile boolean hasInstantiationAwareBeanPostProcessors;

    protected boolean hasInstantiationAwareBeanPostProcessors() {
        return this.hasInstantiationAwareBeanPostProcessors;
    }


    @Nullable
    private ClassLoader tempClassLoader;

    @Override
    @Nullable
    public ClassLoader getTempClassLoader() {
        return this.tempClassLoader;
    }

    @Override
    @Nullable
    public Scope getRegisteredScope(String scopeName) {
        Assert.notNull(scopeName, "Scope identifier must not be null");
        return this.scopes.get(scopeName);
    }

    @Nullable
    private BeanExpressionResolver beanExpressionResolver;

    @Nullable
    protected Object evaluateBeanDefinitionString(@Nullable String value, @Nullable BeanDefinition beanDefinition) {
        if (this.beanExpressionResolver == null) {
            return value;
        }

        Scope scope = null;
        if (beanDefinition != null) {
            String scopeName = beanDefinition.getScope();
            if (scopeName != null) {
                scope = getRegisteredScope(scopeName);
            }
        }
        return this.beanExpressionResolver.evaluate(value, new BeanExpressionContext(this, scope));
    }

    protected abstract Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
            throws RuntimeException;

    protected <T> T doGetBean(
            String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly)
            throws Exception {
        // <1> 获取 `beanName`
        // 因为入参 `name` 可能是别名，也可能是 FactoryBean 类型 Bean 的名称（`&` 开头，需要去除）
        // 所以需要获取真实的 beanName
        String beanName = transformedBeanName(name);
        Object bean;
        // <2> 先从缓存（仅缓存单例 Bean ）中获取 Bean 对象，这里缓存指的是 `3` 个 Map
        // 缓存中也可能是正在初始化的 Bean，可以避免**循环依赖注入**引起的问题
        // Eagerly check singleton cache for manually registered singletons.
        Object sharedInstance = getSingleton(beanName);
        // <3> 若从缓存中获取到对应的 Bean，且 `args` 参数为空
        if (sharedInstance != null && args == null) {
//            if (log.isTraceEnabled()) {
//                if (isSingletonCurrentlyInCreation(beanName)) {
//                    log.trace("Returning eagerly cached instance of singleton bean '" + beanName +
//                            "' that is not fully initialized yet - a consequence of a circular reference");
//                } else {
//                    log.trace("Returning cached instance of singleton bean '" + beanName + "'");
//                }
//            }
            // <3.1> 获取 Bean 的目标对象，`scopedInstance` 非 FactoryBean 类型直接返回
            // 否则，调用 FactoryBean#getObject() 获取目标对象
            // 不管是从缓存中获取的还是新创建的，都会调用这个方法进行处理，如果是 FactoryBean 类型则调用其 getObject() 获取目标对象
            bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);
        }
        // 缓存中没有对应的 Bean，则开启 Bean 的加载
        else {
            // Fail if we're already creating this bean instance:
            // We're assumably within a circular reference.
            // <4> 如果**非单例模式**下的 Bean 正在创建，这里又开始创建，表明存在循环依赖，则直接抛出异常
            if (isPrototypeCurrentlyInCreation(beanName)) {
//				prototypesCurrentlyInCreation 中保存非单例模式下正在创建的 Bean 的名称，这里又重新创建，表示出现循环依赖，则直接抛出异常
//				Spring 对于非单例模式的 Bean 无法进行相关缓存，也就无法处理循环依赖的情况，选择了直接抛出异常
                throw new RuntimeException(beanName);
            }

            // Check if bean definition exists in this factory.
            BeanFactory parentBeanFactory = getParentBeanFactory();
            // <5> 如果从当前容器中没有找到对应的 BeanDefinition，则从父容器中加载（如果存在父容器）
            if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
                // Not found -> check parent.
                // <5.1> 获取 `beanName`，因为可能是别名，则进行处理
                // 和第 `1` 步不同，不需要对 `&` 进行处理，因为进入父容器重新依赖查找
                String nameToLookup = originalBeanName(name);
                // <5.2> 若为 AbstractBeanFactory 类型，委托父容器的 doGetBean 方法进行处理
                // 否则，就是非 Spring IoC 容器，根据参数调用相应的 `getBean(...)`方法
                if (parentBeanFactory instanceof AbstractBeanFactory) {
                    return ((AbstractBeanFactory) parentBeanFactory).doGetBean(
                            nameToLookup, requiredType, args, typeCheckOnly);
                } else if (args != null) {
                    // Delegation to parent with explicit args.
                    return (T) parentBeanFactory.getBean(nameToLookup, args);
                } else if (requiredType != null) {
                    // No args -> delegate to standard getBean method.
                    return parentBeanFactory.getBean(nameToLookup, requiredType);
                } else {
                    return (T) parentBeanFactory.getBean(nameToLookup);
                }
            }
            // <6> 如果不是仅仅做类型检查，则表示需要创建 Bean，将 `beanName` 标记为已创建过
            if (!typeCheckOnly) {
                markBeanAsCreated(beanName);
            }

            try {
                // <7> 从容器中获取 `beanName` 对应的的 RootBeanDefinition（合并后）
                RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
                // 检查是否为抽象类
                checkMergedBeanDefinition(mbd, beanName, args);

                // Guarantee initialization of beans that the current bean depends on.
                // 每个 Bean 不一定是单独工作的，可以通过 depends-on 配置依赖的 Bean，其他 Bean 也可以依赖它
                // 对于依赖的 Bean，会优先加载，所以在 Spring 的加载顺序中，在初始化某个 Bean 的时候，首先会初始化这个 Bean 的依赖
                // <8> 获取当前正在创建的 Bean 所依赖对象集合（`depends-on` 配置的依赖）
                String[] dependsOn = mbd.getDependsOn();
                if (dependsOn != null) {
                    for (String dep : dependsOn) {
                        // <8.1> 检测是否存在循环依赖，存在则抛出异常
                        if (isDependent(beanName, dep)) {
                            throw new RuntimeException(mbd.getResourceDescription() + beanName +
                                    "Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
                        }
                        // <8.2> 将 `beanName` 与 `dep` 之间依赖的关系进行缓存
                        registerDependentBean(dep, beanName);
                        try {
                            // <8.3> 先创建好依赖的 Bean（重新调用 `getBean(...)` 方法）
                            getBean(dep);
                        } catch (Exception ex) {
                            throw new RuntimeException(mbd.getResourceDescription() + beanName +
                                    "'" + beanName + "' depends on missing bean '" + dep + "'", ex);
                        }
                    }
                }

//				不同作用域的 Bean 的创建
//				Spring 的作用域划分为三种：单例模式、原型模式、其他模式，会依次进行判断，然后进行创建，创建过程都是一样的，主要是存储范围不一样
//				单例模式：一个 BeanFactory 有且仅有一个实例
//				原型模式：每次依赖查找和依赖注入生成新 Bean 对象
//				其他模式，例如 request 作用域会将 Bean 存储在 ServletRequest 上下文中；session 作用域会将 Bean 存储在 HttpSession 中；
//				application 作用域会将 Bean 存储在 ServletContext 中
                // Create bean instance.
                // <9> 开始创建 Bean，不同模式创建方式不同
                if (mbd.isSingleton()) {// <9.1> 单例模式
                    /*
                     * <9.1.1> 创建 Bean，成功创建则进行缓存，并移除缓存的早期对象
                     * 创建过程实际调用的下面这个 `createBean(...)` 方法
                     */
                    sharedInstance = getSingleton(beanName, () -> {
                        // ObjectFactory 实现类
                        try {
                            // **【核心】** 创建 Bean
                            return createBean(beanName, mbd, args);
                        } catch (Exception ex) {
                            // Explicitly remove instance from singleton cache: It might have been put there
                            // eagerly by the creation process, to allow for circular reference resolution.
                            // Also remove any beans that received a temporary reference to the bean.
                            // 如果创建过程出现异常，则显式地从缓存中删除当前 Bean 相关信息
                            // 在单例模式下为了解决循环依赖，创建过程会缓存早期对象，这里需要进行删除
                            destroySingleton(beanName);
                            throw ex;
                        }
                    });
                    // <9.1.2> 获取 Bean 的目标对象，`scopedInstance` 非 FactoryBean 类型直接返回
                    // 否则，调用 FactoryBean#getObject() 获取目标对象
                    bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
                }
                // <9.2> 原型模式
                else if (mbd.isPrototype()) {
                    // It's a prototype -> create a new instance.
                    Object prototypeInstance = null;
                    try {
                        // <9.2.1> 将 `beanName` 标记为原型模式正在创建
                        beforePrototypeCreation(beanName);
                        // <9.2.2> **【核心】** 创建 Bean ，这里没有缓存，每次加载 Bean 都会创建一个对象
                        prototypeInstance = createBean(beanName, mbd, args);
                    } finally {
                        // <9.2.3> 将 `beanName` 标记为不在创建中，照应第 `9.2.1` 步
                        afterPrototypeCreation(beanName);
                    }
                    // <9.2.4> 获取 Bean 的目标对象，`scopedInstance` 非 FactoryBean 类型直接返回
                    // 否则，调用 FactoryBean#getObject() 获取目标对象
                    bean = getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
                }
                // <9.3> 其他模式
                else {
                    // <9.3.1> 获取该模式的 Scope 对象 `scope`，不存在则抛出异常
                    String scopeName = mbd.getScope();
                    if (!StringUtils.hasLength(scopeName)) {
                        throw new IllegalStateException("No scope name defined for bean ´" + beanName + "'");
                    }
                    Scope scope = this.scopes.get(scopeName);
                    if (scope == null) {
                        throw new IllegalStateException("No Scope registered for scope name '" + scopeName + "'");
                    }
                    try {
                        // <9.3.1> 从 `scope` 中获取 `beanName` 对应的对象（看你的具体实现），不存在则执行**原型模式**的四个步骤进行创建
                        //--想要自定义一个作用域，可以实现 org.springframework.beans.factory.config.Scope 接口，并往 Spring 应用上下文注册即可
                        Object scopedInstance = scope.get(beanName, () -> {
                            // 将 `beanName` 标记为原型模式正在创建
                            beforePrototypeCreation(beanName);
                            try {
                                // **【核心】** 创建 Bean
                                return createBean(beanName, mbd, args);
                            } finally {
                                // 将 `beanName` 标记为不在创建中，照应上一步
                                afterPrototypeCreation(beanName);
                            }
                        });
                        // 获取 Bean 的目标对象，`scopedInstance` 非 FactoryBean 类型直接返回
                        // 否则，调用 FactoryBean#getObject() 获取目标对象
                        bean = getObjectForBeanInstance(scopedInstance, name, beanName, mbd);
                    } catch (IllegalStateException ex) {
                        throw new RuntimeException(beanName +
                                "Scope '" + scopeName + "' is not active for the current thread; consider " +
                                "defining a scoped proxy for this bean if you intend to refer to it from a singleton",
                                ex);
                    }
                }
            } catch (Exception ex) {
                cleanupAfterBeanCreationFailure(beanName);
                throw ex;
            }
        }

        // Check if required type matches the type of the actual bean instance.
        // <10> 如果入参 `requiredType` 不为空，并且 Bean 不是该类型，则需要进行类型转换
        if (requiredType != null && !requiredType.isInstance(bean)) {
            try {
                // <10.1> 通过类型转换机制，将 Bean 转换成 `requiredType` 类型
                // 对 Spring 的类型转换机制感兴趣可以自己研究，参考 org.springframework.core.convert.support.DefaultConversionService
                T convertedBean = getTypeConverter().convertIfNecessary(bean, requiredType);
                // <10.2> 转换后的 Bean 为空则抛出异常
                if (convertedBean == null) {
                    // 转换失败，抛出 BeanNotOfRequiredTypeException 异常
                    throw new RuntimeException(name + requiredType + bean.getClass());
                }
                // <10.3> 返回类型转换后的 Bean 对象
                return convertedBean;
            } catch (Exception ex) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Failed to convert bean '" + name + "' to required type '" +
                            ClassUtils.getQualifiedName(requiredType) + "'", ex);
                }
                throw new RuntimeException(name + requiredType + bean.getClass());
            }
        }
        // <11> 返回获取到的 Bean
        return (T) bean;
    }

    @Nullable
    protected TypeConverter getCustomTypeConverter() {
        return this.typeConverter;
    }

    protected void cleanupAfterBeanCreationFailure(String beanName) {
        synchronized (this.mergedBeanDefinitions) {
            this.alreadyCreated.remove(beanName);
        }
    }

    protected boolean hasBeanCreationStarted() {
        return !this.alreadyCreated.isEmpty();
    }

    protected Object getObjectForBeanInstance(
            Object beanInstance, String name, String beanName, @Nullable RootBeanDefinition mbd) {

        // Don't let calling code try to dereference the factory if the bean isn't a factory.
        // <1> 若 `name` 以 `&` 开头，说明想要获取 FactoryBean，则校验其**正确性**
        if (BeanFactoryUtils.isFactoryDereference(name)) {
            if (beanInstance instanceof NullBean) {
                // <1.1> 如果是 NullBean 空对象，则直接返回
                return beanInstance;
            }
            // <1.2> 如果不是 FactoryBean 类型，则抛出异常
            if (!(beanInstance instanceof FactoryBean)) {
                throw new RuntimeException(beanName + beanInstance.getClass());
            }
            if (mbd != null) {
                mbd.isFactoryBean = true;
            }
            return beanInstance;
        }

        // Now we have the bean instance, which may be a normal bean or a FactoryBean.
        // If it's a FactoryBean, we use it to create a bean instance, unless the
        // caller actually wants a reference to the factory.
        // 到这里我们就有了一个 Bean，可能是一个正常的 Bean，也可能是一个 FactoryBean
        // 如果是 FactoryBean，则需要通过其 getObject() 方法获取目标对象
        // <2> 如果 `beanInstance` 不是 FactoryBean 类型，不需要再处理则直接返回
        // 或者（表示是 FactoryBean 类型） `name` 以 `&` 开头，表示你想要获取实际 FactoryBean 对象，则直接返回
        // 还不符合条件的话，表示是 FactoryBean，需要获取 getObject() 返回目标对象
        if (!(beanInstance instanceof FactoryBean)) {
            return beanInstance;
        }

        Object object = null;
        if (mbd != null) {
            mbd.isFactoryBean = true;
        }
        // <3> 如果入参没有传 BeanDefinition，则从 `factoryBeanObjectCache` 缓存中获取对应的 Bean 对象
        // 入参传了 BeanDefinition 表示这个 Bean 是刚创建的，不走缓存，需要调用其 getObject() 方法获取目标对象
        // `factoryBeanObjectCache`：FactoryBean#getObject() 调用一次后返回的目标对象缓存在这里
        else {
            object = getCachedObjectForFactoryBean(beanName);
        }
        // <4> 若第 `3` 步获取的对象为空，则需要调用 FactoryBean#getObject() 获得对象
        if (object == null) {
            // Return bean instance from factory.
            // <4.1> 将 `beanInstance` 转换成 FactoryBean 类型
            FactoryBean<?> factory = (FactoryBean<?>) beanInstance;
            // Caches object obtained from FactoryBean if it is a singleton.
            // <4.2> 如果入参没有传 BeanDefinition 并且当前容器存在对应的 BeanDefinition
            if (mbd == null && containsBeanDefinition(beanName)) {
                // 获取对应的 RootBeanDefinition（合并后）
                mbd = getMergedLocalBeanDefinition(beanName);
            }
            // 是否是用户定义的（不是 Spring 创建解析出来的）
            boolean synthetic = (mbd != null && mbd.isSynthetic());
            // <4.3> **【核心】**通过 FactoryBean 获得目标对象，单例模式会缓存在 `factoryBeanObjectCache` 中
            object = getObjectFromFactoryBean(factory, beanName, !synthetic);
        }
        return object;
    }

    @Override
    public TypeConverter getTypeConverter() {
        TypeConverter customConverter = getCustomTypeConverter();
        if (customConverter != null) {
            return customConverter;
        } else {
            // Build default TypeConverter, registering custom editors.
            SimpleTypeConverter typeConverter = new SimpleTypeConverter();
            typeConverter.setConversionService(getConversionService());
            // registerCustomEditors(typeConverter);
            return typeConverter;
        }
    }

}
