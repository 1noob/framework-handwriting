package com.springframework.beans.factory.support;


import com.springframework.beans.*;
import com.springframework.beans.config.BeanDefinition;
import com.springframework.beans.factory.*;
import com.springframework.beans.factory.config.*;
import com.springframework.core.*;
import com.springframework.core.convert.ConversionService;
import com.springframework.util.*;
import com.sun.istack.internal.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @Author 虎哥
 * @Description //TODO
 * 要带着问题去学习,多猜想多验证
 **/
@Slf4j
public class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory, BeanDefinitionRegistry {
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public void destroySingletons() {

    }

    public void setAllowCircularReferences(boolean allowCircularReferences) {
        this.allowCircularReferences = allowCircularReferences;
    }

    @Override
    public boolean isCacheBeanMetadata() {
        return false;
    }

    @Override
    public ConversionService getConversionService() {
        return null;
    }

    @Override
    public ClassLoader getTempClassLoader() {
        return null;
    }

    @Override
    public Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName, Set<String> autowiredBeanNames, TypeConverter typeConverter) throws RuntimeException {
        return null;
    }

    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {

    }

    @Override
    public void setBeanExpressionResolver(BeanExpressionResolver resolver) {

    }

    @Override
    public void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar) {

    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {

    }

    @Override
    public BeanExpressionResolver getBeanExpressionResolver() {
        return null;
    }

    @Override
    public String resolveEmbeddedValue(String value) {
        return null;
    }

    @Override
    public void setTempClassLoader(ClassLoader tempClassLoader) {

    }

    @Override
    public void setConversionService(ConversionService conversionService) {

    }

    @Override
    public void addEmbeddedValueResolver(StringValueResolver valueResolver) {

    }

    @Override
    public int getBeanPostProcessorCount() {
        return 0;
    }

    @Override
    public boolean hasEmbeddedValueResolver() {
        return false;
    }

    @Override
    public void registerScope(String scopeName, Scope scope) {

    }

    private boolean allowRawInjectionDespiteWrapping = false;

    @Override
    public void registerAlias(String beanName, String alias) throws RuntimeException {

    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws RuntimeException {

    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return false;
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return null;
    }

    @Override
    public int getBeanDefinitionCount() {
        return 0;
    }

    @Override
    public BeanFactory getParentBeanFactory() {
        return null;
    }


    @Override
    protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
            throws RuntimeException {

        if (logger.isTraceEnabled()) {
            logger.trace("Creating instance of bean '" + beanName + "'");
        }
        RootBeanDefinition mbdToUse = mbd;

        // Make sure bean class is actually resolved at this point, and
        // clone the bean definition in case of a dynamically resolved Class
        // which cannot be stored in the shared merged bean definition.
        // 创建一个 Java 对象之前，需要确保存在对应的 Class 对象
        // <1> 获取 `mbd` 对应的 Class 对象，确保当前 Bean 能够被创建出来
        Class<?> resolvedClass = resolveBeanClass(mbd, beanName);
        // 如果这里获取到了 Class 对象，但是 `mbd` 中没有 Class 对象的相关信息，表示这个 Class 对象是动态解析出来的
        if (resolvedClass != null && !mbd.hasBeanClass() && mbd.getBeanClassName() != null) {
            // 复制一份 `mbd`，并设置 Class 对象，因为动态解析出来的 Class 对象不被共享
            mbdToUse = new RootBeanDefinition(mbd);
            mbdToUse.setBeanClass(resolvedClass);
        }

        // Prepare method overrides.
        try {
            // <2> 对所有的 MethodOverride 进行验证和准备工作（确保存在对应的方法，并设置为不能重复加载）
            mbdToUse.prepareMethodOverrides();
        } catch (Exception ex) {

            throw new RuntimeException(mbdToUse.getResourceDescription() +
                    beanName + "Validation of method overrides failed", ex);
        }

        try {
            // Give BeanPostProcessors a chance to return a proxy instead of the target bean instance.
            /**
             * <3> 在实例化前进行相关处理，会先调用所有 {@link InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation}
             * 注意，如果这里返回对象不是 `null` 的话，不会继续往下执行原本初始化操作，直接返回，也就是说这个方法返回的是最终实例对象
             * 可以通过这种方式提前返回一个代理对象，例如 AOP 的实现，或者 RPC 远程调用的实现（因为本地类没有远程能力，可以通过这种方式进行拦截）
             */
            Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
            if (bean != null) {
                return bean;
            }
        } catch (Throwable ex) {
            throw new RuntimeException(mbdToUse.getResourceDescription() + beanName +
                    "BeanPostProcessor before instantiation of bean failed" + ex);
        }

        try {
            // <4> 创建 Bean 对象 `beanInstance`，如果上一步没有返回代理对象，就只能走常规的路线进行 Bean 的创建了
            Object beanInstance = doCreateBean(beanName, mbdToUse, args);
            if (logger.isTraceEnabled()) {
                logger.trace("Finished creating instance of bean '" + beanName + "'");
            }
            // <5> 将 `beanInstance` 返回
            return beanInstance;
        } catch (Exception ex) {
            // A previously detected exception with proper bean creation context already,
            // or illegal singleton state to be communicated up to DefaultSingletonBeanRegistry.
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(
                    mbdToUse.getResourceDescription() + beanName + "Unexpected exception during bean creation", ex);
        }
    }

//    @Nullable
//    protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
//        for (BeanPostProcessor bp : getBeanPostProcessors()) {
//            if (bp instanceof InstantiationAwareBeanPostProcessor) {
//                InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
//                Object result = ibp.postProcessBeforeInstantiation(beanClass, beanName);
//                if (result != null) {
//                    return result;
//                }
//            }
//        }
//        return null;
//    }

//    // 遍历所有的 BeanPostProcessor 处理器，执行 postProcessAfterInitialization 方法，初始化后置处理
//    @Override
//    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
//            throws RuntimeException {
//
//        Object result = existingBean;
//        // 遍历 BeanPostProcessor
//        for (BeanPostProcessor processor : getBeanPostProcessors()) {
//            // 处理
//            // 初始化的后置处理，返回 `current` 处理结果
//            Object current = processor.postProcessAfterInitialization(result, beanName);
//            // 返回空，则返回 result
//            if (current == null) {
//                return result;
//            }
//            // 修改 result
//            result = current;
//        }
//        return result;
//    }

    @Nullable
    protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
        Object bean = null;
        if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
            // Make sure bean class is actually resolved at this point.
            // 如果 RootBeanDefinition 不是用户定义的（由 Spring 解析出来的），并且存在 InstantiationAwareBeanPostProcessor 处理器
            if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
                Class<?> targetType = determineTargetType(beanName, mbd);
                if (targetType != null) {
                    // 实例化前置处理
//                    bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
                    if (bean != null) {
                        // 后置处理
                        bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
                    }
                }
            }
            mbd.beforeInstantiationResolved = (bean != null);
        }
        return bean;
    }


    private final ConcurrentMap<Class<?>, Method[]> factoryMethodCandidateCache = new ConcurrentHashMap<>();

    @Nullable
    protected Class<?> getTypeForFactoryMethod(String beanName, RootBeanDefinition mbd, Class<?>... typesToMatch) {
        ResolvableType cachedReturnType = mbd.factoryMethodReturnType;
        if (cachedReturnType != null) {
            return cachedReturnType.resolve();
        }

        Class<?> commonType = null;
        Method uniqueCandidate = mbd.factoryMethodToIntrospect;

        if (uniqueCandidate == null) {
            Class<?> factoryClass;
            boolean isStatic = true;

            String factoryBeanName = mbd.getFactoryBeanName();
            if (factoryBeanName != null) {
                if (factoryBeanName.equals(beanName)) {
                    throw new RuntimeException(mbd.getResourceDescription() + beanName +
                            "factory-bean reference points back to the same bean definition");
                }
                // Check declared factory method return type on factory class.
                factoryClass = getType(factoryBeanName);
                isStatic = false;
            } else {
                // Check declared factory method return type on bean class.
                factoryClass = resolveBeanClass(mbd, beanName, typesToMatch);
            }

            if (factoryClass == null) {
                return null;
            }
            factoryClass = ClassUtils.getUserClass(factoryClass);

            // If all factory methods have the same return type, return that type.
            // Can't clearly figure out exact method due to type converting / autowiring!
            int minNrOfArgs =
                    (mbd.hasConstructorArgumentValues() ? mbd.getConstructorArgumentValues().getArgumentCount() : 0);
            Method[] candidates = this.factoryMethodCandidateCache.computeIfAbsent(factoryClass,
                    clazz -> ReflectionUtils.getUniqueDeclaredMethods(clazz, ReflectionUtils.USER_DECLARED_METHODS));

            for (Method candidate : candidates) {
                if (Modifier.isStatic(candidate.getModifiers()) == isStatic && mbd.isFactoryMethod(candidate) &&
                        candidate.getParameterCount() >= minNrOfArgs) {
                    // Declared type variables to inspect?
                    if (candidate.getTypeParameters().length > 0) {
                        try {
                            // Fully resolve parameter names and argument values.
                            Class<?>[] paramTypes = candidate.getParameterTypes();
                            String[] paramNames = null;
                            ParameterNameDiscoverer pnd = getParameterNameDiscoverer();
                            if (pnd != null) {
                                paramNames = pnd.getParameterNames(candidate);
                            }
                            ConstructorArgumentValues cav = mbd.getConstructorArgumentValues();
                            Set<ConstructorArgumentValues.ValueHolder> usedValueHolders = new HashSet<>(paramTypes.length);
                            Object[] args = new Object[paramTypes.length];
                            for (int i = 0; i < args.length; i++) {
                                ConstructorArgumentValues.ValueHolder valueHolder = cav.getArgumentValue(
                                        i, paramTypes[i], (paramNames != null ? paramNames[i] : null), usedValueHolders);
                                if (valueHolder == null) {
                                    valueHolder = cav.getGenericArgumentValue(null, null, usedValueHolders);
                                }
                                if (valueHolder != null) {
                                    args[i] = valueHolder.getValue();
                                    usedValueHolders.add(valueHolder);
                                }
                            }
                            Class<?> returnType = AutowireUtils.resolveReturnTypeForFactoryMethod(
                                    candidate, args, getBeanClassLoader());
                            uniqueCandidate = (commonType == null && returnType == candidate.getReturnType() ?
                                    candidate : null);
                            commonType = ClassUtils.determineCommonAncestor(returnType, commonType);
                            if (commonType == null) {
                                // Ambiguous return types found: return null to indicate "not determinable".
                                return null;
                            }
                        } catch (Throwable ex) {
//                            if (log.isDebugEnabled()) {
//                                log.debug("Failed to resolve generic return type for factory method: " + ex);
//                            }
                        }
                    } else {
                        uniqueCandidate = (commonType == null ? candidate : null);
                        commonType = ClassUtils.determineCommonAncestor(candidate.getReturnType(), commonType);
                        if (commonType == null) {
                            // Ambiguous return types found: return null to indicate "not determinable".
                            return null;
                        }
                    }
                }
            }

            mbd.factoryMethodToIntrospect = uniqueCandidate;
            if (commonType == null) {
                return null;
            }
        }

        // Common return type found: all factory methods return same type. For a non-parameterized
        // unique candidate, cache the full type declaration context of the target factory method.
        cachedReturnType = (uniqueCandidate != null ?
                ResolvableType.forMethodReturnType(uniqueCandidate) : ResolvableType.forClass(commonType));
        mbd.factoryMethodReturnType = cachedReturnType;
        return cachedReturnType.resolve();
    }

    @Nullable
    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Nullable
    protected ParameterNameDiscoverer getParameterNameDiscoverer() {
        return this.parameterNameDiscoverer;
    }

    @Nullable
    protected Class<?> determineTargetType(String beanName, RootBeanDefinition mbd, Class<?>... typesToMatch) {
        Class<?> targetType = mbd.getTargetType();
        if (targetType == null) {
            targetType = (mbd.getFactoryMethodName() != null ?
                    getTypeForFactoryMethod(beanName, mbd, typesToMatch) :
                    resolveBeanClass(mbd, beanName, typesToMatch));
            if (ObjectUtils.isEmpty(typesToMatch) || getTempClassLoader() == null) {
                mbd.resolvedTargetType = targetType;
            }
        }
        return targetType;
    }
//
//    protected void applyMergedBeanDefinitionPostProcessors(RootBeanDefinition mbd, Class<?> beanType, String beanName) {
//        for (BeanPostProcessor bp : getBeanPostProcessors()) {
//            if (bp instanceof MergedBeanDefinitionPostProcessor) {
//                MergedBeanDefinitionPostProcessor bdp = (MergedBeanDefinitionPostProcessor) bp;
//                bdp.postProcessMergedBeanDefinition(mbd, beanType, beanName);
//            }
//        }
//    }

    private final ConcurrentMap<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();

    protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
            throws RuntimeException {

        // Instantiate the bean.
        /**
         * <1> Bean 的实例化阶段，会将 Bean 的实例对象封装成 {@link BeanWrapperImpl} 包装对象
         * BeanWrapperImpl 承担的角色：
         * 1. Bean 实例的包装
         * 2. {@link org.springframework.beans.PropertyAccessor} 属性编辑器
         * 3. {@link org.springframework.beans.PropertyEditorRegistry} 属性编辑器注册表
         * 4. {@link org.springframework.core.convert.ConversionService} 类型转换器（Spring 3+，替换了之前的 TypeConverter）
         */
        BeanWrapper instanceWrapper = null;
        // <1.1> 如果是单例模式，则先尝试从 `factoryBeanInstanceCache` 缓存中获取实例对象，并从缓存中移除
        if (mbd.isSingleton()) {
            instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
        }
        // <1.2> 使用合适的实例化策略来创建 Bean 的实例：工厂方法、构造函数自动注入、简单初始化
        // 主要是将 BeanDefinition 转换为 BeanWrapper 对象
        if (instanceWrapper == null) {
            instanceWrapper = createBeanInstance(beanName, mbd, args);
        }
        // <1.3> 获取包装的实例对象 `bean`
        Object bean = instanceWrapper.getWrappedInstance();
        // <1.4> 获取包装的实例对象的类型 `beanType`
        Class<?> beanType = instanceWrapper.getWrappedClass();
        if (beanType != NullBean.class) {
            mbd.resolvedTargetType = beanType;
        }

        // Allow post-processors to modify the merged bean definition.
        // <2> 对 RootBeanDefinition（合并后）进行加工处理
        synchronized (mbd.postProcessingLock) {// 加锁，线程安全
            // <2.1> 如果该 RootBeanDefinition 没有处理过，则进行下面的处理
            if (!mbd.postProcessed) {
                try {
                    /**
                     * <2.2> 对 RootBeanDefinition（合并后）进行加工处理
                     * 调用所有 {@link MergedBeanDefinitionPostProcessor#postProcessMergedBeanDefinition}
                     * 【重要】例如有下面两个处理器：
                     * 1. AutowiredAnnotationBeanPostProcessor 会先解析出 @Autowired 和 @Value 注解标注的属性的注入元信息，后续进行依赖注入；
                     * 2. CommonAnnotationBeanPostProcessor 会先解析出 @Resource 注解标注的属性的注入元信息，后续进行依赖注入，
                     * 它也会找到 @PostConstruct 和 @PreDestroy 注解标注的方法，并构建一个 LifecycleMetadata 对象，用于后续生命周期中的初始化和销毁
                     */
//                    applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
                } catch (Throwable ex) {
                    throw new RuntimeException(mbd.getResourceDescription() + beanName +
                            "Post-processing of merged bean definition failed", ex);
                }
                // <2.3> 设置该 RootBeanDefinition 被处理过，避免重复处理
                mbd.postProcessed = true;
            }
        }

        // Eagerly cache singletons to be able to resolve circular references
        // even when triggered by lifecycle interfaces like BeanFactoryAware.
        // <3> 提前暴露这个 `bean`，如果可以的话，目的是解决单例模式 Bean 的循环依赖注入
        // <3.1> 判断是否可以提前暴露
        // 如果是单例模式、允许循环依赖（默认为 true）、当前单例 Bean 正在被创建（前面已经标记过），则提前暴露
        boolean earlySingletonExposure = (mbd.isSingleton() // 单例模式
                && this.allowCircularReferences && // 允许循环依赖，默认为 true
                isSingletonCurrentlyInCreation(beanName));// 当前单例 bean 正在被创建，在前面已经标记过
        if (earlySingletonExposure) {
//            if (log.isTraceEnabled()) {
//                log.trace("Eagerly caching bean '" + beanName +
//                        "' to allow for resolving potential circular references");
//            }
            /**
             * <3.2>
             * 创建一个 ObjectFactory 实现类，用于返回当前正在被创建的 `bean`，提前暴露，保存在 `singletonFactories` （**三级 Map**）缓存中
             *
             * 可以回到前面的 {@link AbstractBeanFactory#doGetBean#getSingleton(String)} 方法
             * 加载 Bean 的过程会先从缓存中获取单例 Bean，可以避免单例模式 Bean 循环依赖注入的问题
             */
            addSingletonFactory(beanName,
                    () -> getEarlyBeanReference(beanName, mbd, bean));// ObjectFactory 实现类
        }

        // Initialize the bean instance.
        // 开始初始化 `bean`
        Object exposedObject = bean;
        try {
            // <4> 对 `bean` 进行属性填充，注入对应的属性值
            populateBean(beanName, mbd, instanceWrapper);
            // <5> 初始化这个 `exposedObject`，调用其初始化方法
            exposedObject = initializeBean(beanName, exposedObject, mbd);
        } catch (Throwable ex) {

            throw new RuntimeException(
                    mbd.getResourceDescription() + beanName + "Initialization of bean failed", ex);
        }
        // <6> 循环依赖注入的检查
        if (earlySingletonExposure) {
            // <6.1> 获取当前正在创建的 `beanName` 被依赖注入的早期引用
            // 注意，这里有一个入参是 `false`，不会调用上面第 `3` 步的 ObjectFactory 实现类
            // 也就是说当前 `bean` 如果出现循环依赖注入，这里才能获取到提前暴露的引用
            Object earlySingletonReference = getSingleton(beanName, false);
            // <6.2> 如果出现了循环依赖注入，则进行接下来的检查工作
            if (earlySingletonReference != null) {
                // <6.2.1> 如果 `exposedObject` 没有在初始化阶段中被改变，也就是没有被增强
                // 则使用提前暴露的那个引用
                if (exposedObject == bean) {
                    exposedObject = earlySingletonReference;
                }
                // <6.2.2> 否则，`exposedObject` 已经不是被别的 Bean 依赖注入的那个 Bean
                else if (!this.allowRawInjectionDespiteWrapping  // 是否允许注入未加工的 Bean，默认为 false，这里取非就为 true
                        && hasDependentBean(beanName)) { // 存在依赖 `beanName` 的 Bean（通过 `depends-on` 配置）
                    // 获取依赖当前 `beanName` 的 Bean 们的名称（通过 `depends-on` 配置）
                    String[] dependentBeans = getDependentBeans(beanName);
                    Set<String> actualDependentBeans = new LinkedHashSet<>(dependentBeans.length);
                    // 接下来进行判断，如果依赖 `beanName` 的 Bean 已经创建
                    // 说明当前 `beanName` 被注入了，而这里最终的 `bean` 被包装过，不是之前被注入的
                    // 则抛出异常
                    for (String dependentBean : dependentBeans) {
                        if (!removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
                            actualDependentBeans.add(dependentBean);
                        }
                    }
                    if (!actualDependentBeans.isEmpty()) {
                        throw new RuntimeException(beanName +
                                "Bean with name '" + beanName + "' has been injected into other beans [" +
                                StringUtils.collectionToCommaDelimitedString(actualDependentBeans) +
                                "] in its raw version as part of a circular reference, but has eventually been " +
                                "wrapped. This means that said other beans do not use the final version of the " +
                                "bean. This is often the result of over-eager type matching - consider using " +
                                "'getBeanNamesForType' with the 'allowEagerInit' flag turned off, for example.");
                    }
                }
            }
        }

//		过程大致如下：
//		获取当前正在创建的 beanName 被依赖注入的早期引用，这里调用方法也就是从缓存中获取单例 Bean 的方法。
//		注意，这里有一个入参是 false，不会调用前面 6. 提前暴露当前 Bean 小节中缓存的 ObjectFactory 实现类，也就是说当前 bean 如果出现循环依赖注入，
//		这里才能获取到提前暴露的引用
//		如果上一步获取到了提前暴露的引用，也就是出现了循环依赖注入，则进行接下来的检查工作
//		如果 exposedObject 没有在初始化阶段中被改变，也就是没有被增强，则使用提前暴露的那个引用
//		否则，exposedObject 已经不是被别的 Bean 依赖注入的那个 Bean，则进行相关判断
//		当出现循环依赖注入，这里会检查填充属性和初始化的过程中是否改变了这个 beanName，改变了的话需要判断依赖当前 beanName 的 Bean 们是否已经创建了，
//		如果已经创建了，那么可能它拿到的 beanName 不是这里初始化后的对象（被修改了），所以需要抛出异常

        // Register bean as disposable.
        try {
            /**
             * <7> 为当前 `bean` 注册 DisposableBeanAdapter（如果需要的话），用于 Bean 生命周期中的销毁阶段
             * 可以看到 {@link DefaultSingletonBeanRegistry#destroySingletons()} 方法
             */
//            registerDisposableBeanIfNecessary(beanName, bean, mbd);
        } catch (Exception ex) {
            throw new RuntimeException(
                    mbd.getResourceDescription() + beanName + "Invalid destruction signature", ex);
        }
        // <8> 返回创建好的 `exposedObject` 对象
//		经过上面一系列的过程，实例化、属性填充、初始化等阶段，已经创建好了这个 Bean，最后直接返回
        return exposedObject;
    }

    protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {
        if (System.getSecurityManager() != null) { // 安全模式
            // <1> Aware 接口的回调
            AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                invokeAwareMethods(beanName, bean);
                return null;
            }, getAccessControlContext());
        } else {
            // <1> Aware 接口的回调
            invokeAwareMethods(beanName, bean);
        }
        /**
         * <2> **初始化**阶段的**前置处理**，执行所有 BeanPostProcessor 的 postProcessBeforeInitialization 方法
         *
         * 在 {@link AbstractApplicationContext#prepareBeanFactory} 方法中会添加 {@link ApplicationContextAwareProcessor} 处理器
         * 用于处理其他 Aware 接口的回调，例如ApplicationContextAware、EnvironmentAware、ApplicationEventPublisherAware
         *
         * 在 {@link AnnotationConfigUtils#registerAnnotationConfigProcessors} 方法中会注册 {@link CommonAnnotationBeanPostProcessor} 处理器
         * 在这里会执行 @PostConstruct 注解标注的方法
         */
        Object wrappedBean = bean;
        if (mbd == null || !mbd.isSynthetic()) {
            wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
        }

        try {
            // <3> 激活自定义的初始化方法
            invokeInitMethods(beanName, wrappedBean, mbd);
        } catch (Throwable ex) {
            throw new RuntimeException(
                    (mbd != null ? mbd.getResourceDescription() : null) +
                            beanName + "Invocation of init method failed" + ex);
        }
        /**
         * <4> **初始化**阶段的**后置处理**，执行所有 BeanPostProcessor 的 postProcessAfterInitialization 方法
         *
         * 在 {@link AbstractApplicationContext#prepareBeanFactory} 方法中会添加 {@link ApplicationListenerDetector} 处理器
         * 如果是单例 Bean 且为 ApplicationListener 类型，则添加到 Spring 应用上下文，和 Spring 事件相关
         */
        if (mbd == null || !mbd.isSynthetic()) {
            wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
        }

        return wrappedBean;
    }

    @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws RuntimeException {
        return existingBean;
    }

    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
            throws RuntimeException {

        Object result = existingBean;
//        // 遍历所有 BeanPostProcessor
//        for (BeanPostProcessor processor : getBeanPostProcessors()) {
//            // 初始化的前置处理，返回 `current` 处理结果
//            Object current = processor.postProcessBeforeInitialization(result, beanName);
//            // 处理结果为空，则直接返回 `result`
//            if (current == null) {
//                return result;
//            }
//            // 否则，`result` 复制 `current`
//            result = current;
//        }
        return result;
    }

    @Override
    public <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws RuntimeException {
        return null;
    }

    // 如果是 BeanNameAware、BeanClassLoaderAware 或 BeanFactoryAware，则调用其 setXxx 方法
    private void invokeAwareMethods(String beanName, Object bean) {
        if (bean instanceof Aware) {
            if (bean instanceof BeanNameAware) {
                ((BeanNameAware) bean).setBeanName(beanName);
            }
            if (bean instanceof BeanClassLoaderAware) {
                ClassLoader bcl = getBeanClassLoader();
                if (bcl != null) {
                    ((BeanClassLoaderAware) bean).setBeanClassLoader(bcl);
                }
            }
            if (bean instanceof BeanFactoryAware) {
                ((BeanFactoryAware) bean).setBeanFactory(AbstractAutowireCapableBeanFactory.this);
            }
        }
    }

    protected void invokeInitMethods(String beanName, Object bean, @Nullable RootBeanDefinition mbd)
            throws Throwable {
        // <1> InitializingBean 接口的回调（如果是）
        boolean isInitializingBean = (bean instanceof InitializingBean);
        if (isInitializingBean && (mbd == null || !mbd.isExternallyManagedInitMethod("afterPropertiesSet"))) {
//            if (log.isTraceEnabled()) {
//                log.trace("Invoking afterPropertiesSet() on bean with name '" + beanName + "'");
//            }
            if (System.getSecurityManager() != null) {// 安全模式
                try {
                    AccessController.doPrivileged((PrivilegedExceptionAction<Object>) () -> {
                        // 调用其 afterPropertiesSet() 方法
                        ((InitializingBean) bean).afterPropertiesSet();
                        return null;
                    }, getAccessControlContext());
                } catch (PrivilegedActionException pae) {
                    throw pae.getException();
                }
            } else {
                // 调用其 afterPropertiesSet() 方法
                ((InitializingBean) bean).afterPropertiesSet();
            }
        }

        if (mbd != null && bean.getClass() != NullBean.class) {
            String initMethodName = mbd.getInitMethodName();
            if (StringUtils.hasLength(initMethodName) &&
                    !(isInitializingBean && "afterPropertiesSet".equals(initMethodName)) &&
                    !mbd.isExternallyManagedInitMethod(initMethodName)) {
                // <2> 调用通过 `init-method` 指定的初始化方法（反射机制）
                invokeCustomInitMethod(beanName, bean, mbd);
            }
        }
    }

    protected void invokeCustomInitMethod(String beanName, Object bean, RootBeanDefinition mbd)
            throws Throwable {

        String initMethodName = mbd.getInitMethodName();
        Assert.state(initMethodName != null, "No init method set");
        Method initMethod = (mbd.isNonPublicAccessAllowed() ?
                BeanUtils.findMethod(bean.getClass(), initMethodName) :
                ClassUtils.getMethodIfAvailable(bean.getClass(), initMethodName));

        if (initMethod == null) {
            if (mbd.isEnforceInitMethod()) {
                throw new RuntimeException("Could not find an init method named '" +
                        initMethodName + "' on bean with name '" + beanName + "'");
            } else {
//                if (log.isTraceEnabled()) {
//                    log.trace("No default init method named '" + initMethodName +
//                            "' found on bean with name '" + beanName + "'");
//                }
                // Ignore non-existent default lifecycle methods.
                return;
            }
        }

//        if (log.isTraceEnabled()) {
//            log.trace("Invoking init method  '" + initMethodName + "' on bean with name '" + beanName + "'");
//        }
        Method methodToInvoke = ClassUtils.getInterfaceMethodIfPossible(initMethod);

        if (System.getSecurityManager() != null) {
            AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                ReflectionUtils.makeAccessible(methodToInvoke);
                return null;
            });
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)
                        () -> methodToInvoke.invoke(bean), getAccessControlContext());
            } catch (PrivilegedActionException pae) {
                InvocationTargetException ex = (InvocationTargetException) pae.getException();
                throw ex.getTargetException();
            }
        } else {
            try {

                ReflectionUtils.makeAccessible(methodToInvoke);
                methodToInvoke.invoke(bean);
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }

    protected void populateBean(String beanName, RootBeanDefinition mbd, @Nullable BeanWrapper bw) throws Exception {
        // <1> 如果实例对象为空，则进行下面的判断
        if (bw == null) {
            // <1.1> 这个 Bean 有属性，则抛出异常
            if (mbd.hasPropertyValues()) {
                throw new RuntimeException(
                        mbd.getResourceDescription() + beanName + "Cannot apply property values to null instance");
            }
            // <1.2> 否则，不用属性填充，直接 `return`
            else {
                // Skip property population phase for null instance.
                return;
            }
        }

        // Give any InstantiationAwareBeanPostProcessors the opportunity to modify the
        // state of the bean before properties are set. This can be used, for example,
        // to support styles of field injection.
        // <2> 实例化阶段的后置处理，如果满足这两个条件
        if (!mbd.isSynthetic() // RootBeanDefinition 不是用户定义的（由 Spring 解析出来的）
                && hasInstantiationAwareBeanPostProcessors()) { // 是否有 InstantiationAwareBeanPostProcessor 处理器
//            for (BeanPostProcessor bp : getBeanPostProcessors()) {
//                // <2.1> 遍历所有的 BeanPostProcessor
//                // 如果为 InstantiationAwareBeanPostProcessor 类型
//                if (bp instanceof InstantiationAwareBeanPostProcessor) {
//                    // <2.2> 对实例化对象进行后置处理
//                    // 注意如果返回 false，直接 `return`，不会调用后面的 InstantiationAwareBeanPostProcessor 处理器，也不会进行接下来的属性填充
//                    InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
//                    if (!ibp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) {
//                        return;
//                    }
//                }
//            }
        }
        // <3> 获取 `pvs`，承载当前对象的属性值
        PropertyValues pvs = (mbd.hasPropertyValues() ? mbd.getPropertyValues() : null);
        // <4> 获取这个 Bean 的注入模式，默认为 **AUTOWIRE_NO**，例如可以通过 `@Bean` 注解的 `autowire` 属性配置注入模式
        int resolvedAutowireMode = mbd.getResolvedAutowireMode();
        // <4.1> 如果注入模式为 **AUTOWIRE_BY_NAME** 或者 **AUTOWIRE_BY_TYPE**，则通过下面的方式获取属性值
        if (resolvedAutowireMode == AUTOWIRE_BY_NAME || resolvedAutowireMode == AUTOWIRE_BY_TYPE) {
            // <4.2> 将 `pvs` 封装成 MutablePropertyValues 对象 `newPvs`（允许对属性进行相关操作）
            MutablePropertyValues newPvs = new MutablePropertyValues(pvs);
            // Add property values based on autowire by name if applicable.
            // <4.3> **AUTOWIRE_BY_NAME** 模式，通过名称获取相关属性值，保存在 `newPvs` 中
            if (resolvedAutowireMode == AUTOWIRE_BY_NAME) {
                autowireByName(beanName, mbd, bw, newPvs);
            }
            // Add property values based on autowire by type if applicable.
            // <4.4> **AUTOWIRE_BY_TYPE** 模式，通过类型获取相关属性值，保存在 `newPvs` 中
            if (resolvedAutowireMode == AUTOWIRE_BY_TYPE) {
                autowireByType(beanName, mbd, bw, newPvs);
            }
            // <4.5> 将 `newPvs` 复制给 `pvs`
            pvs = newPvs;
        }
        // 是否有 InstantiationAwareBeanPostProcessor 处理器
        boolean hasInstAwareBpps = hasInstantiationAwareBeanPostProcessors();
        // 是否需要进行依赖检查，默认为 true
        boolean needsDepCheck = (mbd.getDependencyCheck() != AbstractBeanDefinition.DEPENDENCY_CHECK_NONE);

        PropertyDescriptor[] filteredPds = null;
        // <5> 通过 InstantiationAwareBeanPostProcessor 处理器（如果有）对 `pvs` 进行处理
        if (hasInstAwareBpps) {
            if (pvs == null) {
                pvs = mbd.getPropertyValues();
            }
            // <5.1> 遍历所有的 BeanPostProcessor
//            for (BeanPostProcessor bp : getBeanPostProcessors()) {
//                // 如果为 InstantiationAwareBeanPostProcessor 类型
//                if (bp instanceof InstantiationAwareBeanPostProcessor) {
//                    InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
            /**
             * Spring 内部的 InstantiationAwareBeanPostProcessor 处理器：
             * {@link AutowiredAnnotationBeanPostProcessor#postProcessProperties} 会解析 @Autowired 和 @Value 注解标注的属性，获取对应属性值；
             * {@link org.springframework.context.annotation.CommonAnnotationBeanPostProcessor#postProcessProperties} 会 解析 Resource 注解标注的属性，获取对应的属性值
             */
            // <5.2> 调用处理器的 `postProcessProperties(...)` 方法，对 `pvs` 进行后置处理
//                    MyPropertyValues pvsToUse = ibp.postProcessProperties(pvs, bw.getWrappedInstance(), beanName);
            // <5.3> 如果上一步的处理结果为空，可能是新版本导致的（Spring 5.1 之前没有上面这个方法），则需要兼容老版本
//                    if (pvsToUse == null) {
//                        // <5.3.1> 找到这个 Bean 的所有 `java.beans.PropertyDescriptor` 属性描述器（包含这个属性的所有信息）
//                        if (filteredPds == null) {
////                            filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
//                        }
//                        // <5.3.2> 调用处理器的 `postProcessPropertyValues(...)` 方法，对 `pvs` 进行后置处理
////                        pvsToUse = ibp.postProcessPropertyValues(pvs, filteredPds, bw.getWrappedInstance(), beanName);
//                        // <5.3.3> 如果处理后的 PropertyValues 对象为空，直接 `return`，则不会调用后面的 InstantiationAwareBeanPostProcessor 处理器，
//                        // 也不会进行接下来的属性填充
//                        if (pvsToUse == null) {
//                            return;
//                        }
//                    }
            // <5.4> 将处理后的 `pvsToUse` 复制给 `pvs`
//                    pvs = pvsToUse;
        }
//            }
//        }
        // <6> 依赖检查
        if (needsDepCheck) {
            // <6.1> 找到这个 Bean 的所有 `java.beans.PropertyDescriptor` 属性描述器（包含这个属性的所有信息）
//            if (filteredPds == null) {
//                filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
//            }
            // <6.2> 依赖检查，如果没有找到对应的属性值，则根据检查策略进行抛出异常（默认不会）
//            checkDependencies(beanName, mbd, filteredPds, pvs);
        }
        // <7> 如果 `pvs` 不为空，则将里面的属性值设置到当前 Bean 对应的属性中（依赖注入）
        // 前面找到的属性值并没有设置到 Bean 中，且属性值可能是一个表达式，类型也可能也不对，需要先进行处理和类型转换，然后再设置到该实例对象中
        if (pvs != null) {
            applyPropertyValues(beanName, mbd, bw, pvs);
        }
    }

    protected void autowireByType(
            String beanName, AbstractBeanDefinition mbd, BeanWrapper bw, MutablePropertyValues pvs) {
        // <1> 获取 TypeConverter 类型转换器，用于取代默认的 PropertyEditor 类型转换器
        // 例如 Spring 3.0 之后的 ConversionService
        TypeConverter converter = getCustomTypeConverter();
//        if (converter == null) {
//            converter = bw;
//        }

        Set<String> autowiredBeanNames = new LinkedHashSet<>(4);
        // <2> 获取当前 Bean 中不满意的非简单类型的属性名称，也就是没有定义属性值的"对象"属性
        String[] propertyNames = unsatisfiedNonSimpleProperties(mbd, bw);
        // <3> 遍历这些对象属性的名称
        for (String propertyName : propertyNames) {
            try {
                // <3> 获取这个属性的 `java.beans.PropertyDescriptor` 属性描述器（包含这个属性的所有信息）
                PropertyDescriptor pd = bw.getPropertyDescriptor(propertyName);
                // Don't try autowiring by type for type Object: never makes sense,
                // even if it technically is a unsatisfied, non-simple property.
                // <4> 如果不是 Object 类型（对 Object 类类型的 Bean 进行自动装配毫无意义），则尝试找到对应的对象
                if (Object.class != pd.getPropertyType()) {
                    // <5> 找到这个属性的写方法
                    MethodParameter methodParam = BeanUtils.getWriteMethodParameter(pd);
                    // Do not allow eager init for type matching in case of a prioritized post-processor.
                    // 是否可以提前初始化
                    boolean eager = !(bw.getWrappedInstance() instanceof PriorityOrdered);
                    // <6> 创建对应的依赖注入描述对象
                    DependencyDescriptor desc = new AbstractAutowireCapableBeanFactory.AutowireByTypeDependencyDescriptor(methodParam, eager);
                    // <7> 依赖注入，找到该属性对应的对象
                    Object autowiredArgument = resolveDependency(desc, beanName, autowiredBeanNames, converter);
                    // <8> 如果找到属性对象，则将该其添加至 `pvs`
                    if (autowiredArgument != null) {
                        pvs.add(propertyName, autowiredArgument);
                    }
                    // <9> 将注入的属性对象和当前 Bean 之前的关系保存起来
                    // 因为该属性可能是一个集合，找到了多个对象，所以这里是一个数组
                    for (String autowiredBeanName : autowiredBeanNames) {
                        // 将 `autowiredBeanName` 与 `beanName` 的依赖关系保存
                        registerDependentBean(autowiredBeanName, beanName);
//                        if (log.isTraceEnabled()) {
//                            log.trace("Autowiring by type from bean name '" + beanName + "' via property '" +
//                                    propertyName + "' to bean named '" + autowiredBeanName + "'");
//                        }
                    }
                    // 清空 `autowiredBeanName` 数组
                    autowiredBeanNames.clear();
                }
            } catch (Exception ex) {
                throw new RuntimeException(mbd.getResourceDescription() + beanName + propertyName, ex);
            }
        }
    }

    protected void autowireByName(
            String beanName, AbstractBeanDefinition mbd, BeanWrapper bw, MutablePropertyValues pvs) throws Exception {
        // <1> 获取当前 Bean 中不满意的非简单类型的属性名称，也就是没有定义属性值的"对象"属性
        String[] propertyNames = unsatisfiedNonSimpleProperties(mbd, bw);
        // <2> 遍历这些对象属性的名称
        for (String propertyName : propertyNames) {
            // <3> 如果当前容器存在对应的 Bean（通过名称判断）
            if (containsBean(propertyName)) {
                // <3.1> 根据属性名称获取对应的 `bean` 对象（依赖查找）
                Object bean = getBean(propertyName);
                // <3.2> 将 `bean` 添加至 `pvs`
                pvs.add(propertyName, bean);
                // <3.3> 将两个 Bean 之间的依赖关系保存起来
                registerDependentBean(propertyName, beanName);
//                if (log.isTraceEnabled()) {
//                    log.trace("Added autowiring by name from bean name '" + beanName +
//                            "' via property '" + propertyName + "' to bean named '" + propertyName + "'");
//                }
            } else {
//                if (log.isTraceEnabled()) {
//                    log.trace("Not autowiring property '" + propertyName + "' of bean '" + beanName +
//                            "' by name: no matching bean found");
//                }
            }
        }
    }

    protected String[] unsatisfiedNonSimpleProperties(AbstractBeanDefinition mbd, BeanWrapper bw) {
        Set<String> result = new TreeSet<>();
        // 获取已设置的属性值
        PropertyValues pvs = mbd.getPropertyValues();
        // 找到这个 Bean 的所有 PropertyDescriptor 属性描述器（包含这个属性的所有信息）
        PropertyDescriptor[] pds = bw.getPropertyDescriptors();
        // 遍历所有属性
        for (PropertyDescriptor pd : pds) {
            if (pd.getWriteMethod() != null  // 有可写方法
                    && !isExcludedFromDependencyCheck(pd)// 不忽略
                    && !pvs.contains(pd.getName()) &&  // 没有对应的属性值
                    !BeanUtils.isSimpleProperty(pd.getPropertyType())) {// 不是简单类型（例如一个实体类）
                result.add(pd.getName());
            }
        }
        // 返回这些不满意的非简单类型的属性
        return StringUtils.toStringArray(result);
    }

    private final Set<Class<?>> ignoredDependencyTypes = new HashSet<>();
    private final Set<Class<?>> ignoredDependencyInterfaces = new HashSet<>();

    protected boolean isExcludedFromDependencyCheck(PropertyDescriptor pd) {
        return (AutowireUtils.isExcludedFromDependencyCheck(pd) ||
                this.ignoredDependencyTypes.contains(pd.getPropertyType()) ||
                AutowireUtils.isSetterDefinedInInterface(pd, this.ignoredDependencyInterfaces));
    }

    protected PropertyDescriptor[] filterPropertyDescriptorsForDependencyCheck(BeanWrapper bw) {
        List<PropertyDescriptor> pds = new ArrayList<>(Arrays.asList(bw.getPropertyDescriptors()));
        pds.removeIf(this::isExcludedFromDependencyCheck);
        return pds.toArray(new PropertyDescriptor[0]);
    }

    @Override
    public Object getBean(String name, Object... args) throws RuntimeException {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws RuntimeException {
        return null;
    }


    @Override
    public Class<?> getType(String name, boolean allowFactoryBeanInit) throws RuntimeException {
        return null;
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws Exception {
        return false;
    }


    @Override
    public Object getBean(Class<?> returnType, Object[] argsToUse) {
        return null;
    }

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {

    }

    private static class AutowireByTypeDependencyDescriptor extends DependencyDescriptor {

        public AutowireByTypeDependencyDescriptor(MethodParameter methodParameter, boolean eager) {
            super(methodParameter, false, eager);
        }
    }

    protected void applyPropertyValues(String beanName, BeanDefinition mbd, BeanWrapper bw, PropertyValues pvs) {
        // <1> 没有相关属性值，则直接 `return` 返回
        if (pvs.isEmpty()) {
            return;
        }

        if (System.getSecurityManager() != null && bw instanceof BeanWrapperImpl) {
            ((BeanWrapperImpl) bw).setSecurityContext(getAccessControlContext());
        }
        // ------------------------开始属性值的转换与填充------------------------
        MutablePropertyValues mpvs = null;
        // 定义一个 `original` 集合，承载属性值（未进行转换）
        List<PropertyValue> original;
        // <2> 如果 `pvs` 是 MutablePropertyValues 类型，则可能已经处理过了
        if (pvs instanceof MutablePropertyValues) {
            mpvs = (MutablePropertyValues) pvs;
            if (mpvs.isConverted()) {
                // Shortcut: use the pre-converted values as-is.
                try {
                    // <2.1> 属性值已经转换了，则将这些属性值设置到当前 Bean 中（反射机制），依赖注入的最终实现！！！
                    bw.setPropertyValues2(mpvs);
                    return;
                } catch (Exception ex) {
                    throw new RuntimeException(
                            mbd.getResourceDescription() + beanName + "Error setting property values", ex);
                }
            }
            // <2.2> 没有转换过，则获取所有的属性值集合
            original = mpvs.getPropertyValueList();
        } else {
            // <2.2> 获取所有的属性值集合
            original = Arrays.asList(pvs.getPropertyValues());
        }
        // 获取 TypeConverter 类型转换器，用于取代默认的 PropertyEditor 类型转换器
        // 例如 Spring 3.0 之后的 ConversionService
        TypeConverter converter = getCustomTypeConverter();
//        if (converter == null) {
//            converter = bw;
//        }
        // 获取对应的解析器
        BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this, beanName, mbd, converter);

        // Create a deep copy, resolving any references for values.
        // <3> 定义一个 `deepCopy` 集合，保存转换后的属性值
        List<PropertyValue> deepCopy = new ArrayList<>(original.size());
        boolean resolveNecessary = false;
        // <4> 遍历所有的属性值，进行转换（如果有必要）
        for (PropertyValue pv : original) {
            // <4.1> 已经转换过，则直接添加到 `deepCopy` 中
            if (pv.isConverted()) {
                deepCopy.add(pv);
            }
            // <4.2> 否则，开始进行转换
            else {
                String propertyName = pv.getName();
                // 转换之前的属性值
                Object originalValue = pv.getValue();
                if (originalValue == AutowiredPropertyMarker.INSTANCE) {
                    Method writeMethod = bw.getPropertyDescriptor(propertyName).getWriteMethod();
                    if (writeMethod == null) {
                        throw new IllegalArgumentException("Autowire marker for property without write method: " + pv);
                    }
                    originalValue = new DependencyDescriptor(new MethodParameter(writeMethod, 0), true);
                }
                // <4.2.1> 表达式的处理（如果有必要的话），例如你在 XML 配置的属性值为 `${systenm.user}`，则会解析出对应的值
                Object resolvedValue = valueResolver.resolveValueIfNecessary(pv, originalValue);
                // 转换之后的属性值
                Object convertedValue = resolvedValue;
                // 该属性是否可以转换
                boolean convertible = bw.isWritableProperty(propertyName) && // 属性可写
                        !PropertyAccessorUtils.isNestedOrIndexedProperty(propertyName);// 不包含 `.` 和 `[`
                if (convertible) {
                    // <4.2.2> 使用类型转换器转换属性值（如果有必要的话）
//                    convertedValue = convertForProperty(resolvedValue, propertyName, bw, converter);
                }
                // Possibly store converted value in merged bean definition,
                // in order to avoid re-conversion for every created bean instance.
                if (resolvedValue == originalValue) {// 属性值没有转换过
                    if (convertible) {
                        // <4.2.3> 设置转换后的值，避免上面的各种判断
                        pv.setConvertedValue(convertedValue);
                    }
                    // <4.2.4> 添加到 `deepCopy` 中
                    deepCopy.add(pv);
                }
                // 属否则属性值进行了转换
                else if (convertible // 可转换的
                        && originalValue instanceof TypedStringValue && // 属性原始值是字符串类型
                        !((TypedStringValue) originalValue).isDynamic() &&  // 属性的原始类型值不是动态生成的字符串
                        !(convertedValue instanceof Collection || ObjectUtils.isArray(convertedValue))) { // 属性的原始值不是集合或者数组类型
                    // <4.2.3> 设置转换后的值，避免上面的各种判断
                    pv.setConvertedValue(convertedValue);
                    // <4.2.4> 添加到 `deepCopy` 中
                    deepCopy.add(pv);
                }
                // 否则
                else {
                    // 这个属性每次都要处理，不能缓存
                    resolveNecessary = true;
                    // <4.2.4> 添加到 `deepCopy` 中
                    deepCopy.add(new PropertyValue(pv, convertedValue));
                }
            }
        }
        // <5> 如果属性值不为空，且不需要每次都处理，则设置为已转换
        if (mpvs != null && !resolveNecessary) {
            mpvs.setConverted();
        }

        // Set our (possibly massaged) deep copy.
        try {
            // <6> 将属性值设置到当前 Bean 中（反射机制），依赖注入的最终实现！！！
            bw.setPropertyValues2(new MutablePropertyValues(deepCopy));
        } catch (Exception ex) {
            throw new RuntimeException(
                    mbd.getResourceDescription() + beanName + "Error setting property values" + ex);
        }
    }


    private final NamedThreadLocal<String> currentlyCreatedBean = new NamedThreadLocal<>("Currently created bean");
    private boolean allowCircularReferences = true;

    protected Object getEarlyBeanReference(String beanName, RootBeanDefinition mbd, Object bean) {
        Object exposedObject = bean;
        if (!mbd.isSynthetic() // RootBeanDefinition 不是用户定义的（由 Spring 解析出来的）
                && hasInstantiationAwareBeanPostProcessors()) {
//            for (BeanPostProcessor bp : getBeanPostProcessors()) {
//                if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
//                    SmartInstantiationAwareBeanPostProcessor ibp = (SmartInstantiationAwareBeanPostProcessor) bp;
//                    exposedObject = ibp.getEarlyBeanReference(exposedObject, beanName);
//                }
//            }
        }
        return exposedObject;
    }

    protected BeanWrapper obtainFromSupplier(Supplier<?> instanceSupplier, String beanName) {
        Object instance;

        String outerBean = this.currentlyCreatedBean.get();
        this.currentlyCreatedBean.set(beanName);
        try {
            instance = instanceSupplier.get();
        } finally {
            if (outerBean != null) {
                this.currentlyCreatedBean.set(outerBean);
            } else {
                this.currentlyCreatedBean.remove();
            }
        }

        if (instance == null) {
            instance = new NullBean();
        }
        BeanWrapper bw = new BeanWrapperImpl(instance);
        initBeanWrapper(bw);
        return bw;
    }

    protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) {
        // Make sure bean class is actually resolved at this point.
        //1.确认bean 类属性 已经真正被解析
        // <1> 获取 `beanName` 对应的 Class 对象
        Class<?> beanClass = resolveBeanClass(mbd, beanName);

        if (beanClass != null && !Modifier.isPublic(beanClass.getModifiers()) && !mbd.isNonPublicAccessAllowed()) {
            throw new RuntimeException(mbd.getResourceDescription() + beanName +
                    "Bean class isn't public, and non-public access not allowed: " + beanClass.getName());
        }
        // <2> 如果存在 Supplier 实例化回调接口，则使用给定的回调方法创建一个实例对象
        Supplier<?> instanceSupplier = mbd.getInstanceSupplier();
        if (instanceSupplier != null) {
            return obtainFromSupplier(instanceSupplier, beanName);
        }
        // <3> 如果配置了 `factory-method` 工厂方法，则调用该方法来创建一个实例对象
        // 通过 @Bean 标注的方法会通过这里进行创建
        //3.静态和实例工厂方法实例化
        // @configuration 中的@bean方法实例化
        if (mbd.getFactoryMethodName() != null) {
            // 这个过程非常复杂，你可以理解为：
            // 找到最匹配的 Method 工厂方法，获取相关参数（依赖注入），然后通过调用该方法返回一个实例对象（反射机制）
            return instantiateUsingFactoryMethod(beanName, mbd, args);
        }

        // Shortcut when re-creating the same bean...
        // <4> 判断这个 RootBeanDefinition 的构造方法是否已经被解析出来了
        // 因为找到最匹配的构造方法比较繁琐，找到后会设置到 RootBeanDefinition 中，避免重复这个过程
        boolean resolved = false;
        boolean autowireNecessary = false;
        if (args == null) {
            synchronized (mbd.constructorArgumentLock) {// 加锁
                // <4.1> 构造方法已经解析出来了
                if (mbd.resolvedConstructorOrFactoryMethod != null) {
                    resolved = true;
                    // <4.2> 这个构造方法有入参，表示需要先获取到对应的入参（构造器注入）
                    autowireNecessary = mbd.constructorArgumentsResolved;
                }
            }
        }
        // <5> 如果最匹配的构造方法已解析出来
        if (resolved) {
            // <5.1> 如果这个构造方法有入参
            if (autowireNecessary) {
                // 这个过程很复杂，你可以理解为：
                // 找到最匹配的构造方法，这里会拿到已经被解析出来的这个方法，并找到入参（构造器注入），然后调用该方法返回一个实例对象（反射机制）
                return autowireConstructor(beanName, mbd, null, null);
            }
            // <5.2> 否则，没有入参
            else {
                // 直接调用解析出来构造方法，返回一个实例对象（反射机制）
                return instantiateBean(beanName, mbd);
            }
        }

        // Candidate constructors for autowiring?
        // <6> 如果最匹配的构造方法还没开始解析，那么需要找到一个最匹配的构造方法，然后创建一个实例对象
        /**
         * <6.1> 尝试通过 SmartInstantiationAwareBeanPostProcessor 处理器的 determineCandidateConstructors 方法来找到一些合适的构造方法
         * 参考 {@link org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#determineCandidateConstructors}
         */
        Constructor<?>[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);
        // <6.2> 是否满足下面其中一个条件
        if (ctors != null  // 上一步找到了合适的构造方法
                || mbd.getResolvedAutowireMode() == AUTOWIRE_CONSTRUCTOR // 构造器注入
                || mbd.hasConstructorArgumentValues()  // 定义了构造方法的入参
                || !ObjectUtils.isEmpty(args)) {  // 当前方法指定了入参
            // 找到最匹配的构造方法，如果 `ctors` 不为空，会从这里面找一个最匹配的，
            // 并找到入参（构造器注入），然后调用该方法返回一个实例对象（反射机制）
            return autowireConstructor(beanName, mbd, ctors, args);
        }

        // Preferred constructors for default construction?
        /**
         * <7> 如果第 `6` 步还不满足，那么尝试获取优先的构造方法
         * 参考 {@link org.springframework.context.support.GenericApplicationContext.ClassDerivedBeanDefinition}
         */
        ctors = mbd.getPreferredConstructors();
        if (ctors != null) {
            // 如果存在优先的构造方法，则从里面找到最匹配的一个，并找到入参（构造器注入），然后调用该方法返回一个实例对象（反射机制）
            return autowireConstructor(beanName, mbd, ctors, null);
        }
        // @configuration注解的类也是在这里实例化的
        // No special handling: simply use no-arg constructor.
        // <8> 如果上面多种情况都不满足，那只能使用兜底方法了，直接调用默认构造方法返回一个实例对象（反射机制）
        return instantiateBean(beanName, mbd);
    }

    protected BeanWrapper autowireConstructor(
            String beanName, RootBeanDefinition mbd, @Nullable Constructor<?>[] ctors, @Nullable Object[] explicitArgs) {

//        return new ConstructorResolver(this).autowireConstructor(beanName, mbd, ctors, explicitArgs);
        return null;
    }

    protected BeanWrapper instantiateUsingFactoryMethod(
            String beanName, RootBeanDefinition mbd, @Nullable Object[] explicitArgs) {

//        return new ConstructorResolver(this).instantiateUsingFactoryMethod(beanName, mbd, explicitArgs);
        return null;
    }

    private InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();

    @Nullable
    protected Constructor<?>[] determineConstructorsFromBeanPostProcessors(@Nullable Class<?> beanClass, String beanName)
            throws RuntimeException {

        if (beanClass != null && hasInstantiationAwareBeanPostProcessors()) {
//            for (BeanPostProcessor bp : getBeanPostProcessors()) {
//                if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
//                    SmartInstantiationAwareBeanPostProcessor ibp = (SmartInstantiationAwareBeanPostProcessor) bp;
//                    Constructor<?>[] ctors = ibp.determineCandidateConstructors(beanClass, beanName);
//                    if (ctors != null) {
//                        return ctors;
//                    }
//                }
//            }
        }
        return null;
    }

    protected InstantiationStrategy getInstantiationStrategy() {
        return this.instantiationStrategy;
    }

    protected BeanWrapper instantiateBean(String beanName, RootBeanDefinition mbd) {
        try {
            Object beanInstance = null;
            if (System.getSecurityManager() != null) {
//                beanInstance = AccessController.doPrivileged(
//                        (PrivilegedAction<Object>) () -> getInstantiationStrategy().instantiate(mbd, beanName, this),
//                        getAccessControlContext());
            } else {
                beanInstance = getInstantiationStrategy().instantiate(mbd, beanName, this);
            }
            BeanWrapper bw = new BeanWrapperImpl(beanInstance);
            initBeanWrapper(bw);
            return bw;
        } catch (Throwable ex) {
            throw new RuntimeException(
                    mbd.getResourceDescription() + beanName + "Instantiation of bean failed", ex);
        }
    }
}
