package com.springframework.beans.factory.support;

import com.springframework.beans.PropertyEditorRegistrar;
import com.springframework.beans.config.BeanDefinition;
import com.springframework.beans.factory.*;
import com.springframework.beans.factory.config.BeanExpressionResolver;
import com.springframework.beans.factory.config.BeanPostProcessor;
import com.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.springframework.core.convert.ConversionService;
import com.springframework.util.Assert;
import com.springframework.util.StringValueResolver;
import com.sun.istack.internal.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
@Slf4j
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements ConfigurableListableBeanFactory, BeanDefinitionRegistry {
    protected final Log logger = LogFactory.getLog(getClass());
    private Comparator<Object> dependencyComparator;
    private AutowireCandidateResolver autowireCandidateResolver = SimpleAutowireCandidateResolver.INSTANCE;

    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);

    public DefaultListableBeanFactory(BeanFactory internalParentBeanFactory) {
        super();
    }

    /**
     * Create a new DefaultListableBeanFactory.
     */
    public DefaultListableBeanFactory() {
        super();
    }

    public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
        this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
    }
    public void setAutowireCandidateResolver(AutowireCandidateResolver autowireCandidateResolver) {
        Assert.notNull(autowireCandidateResolver, "AutowireCandidateResolver must not be null");
        if (autowireCandidateResolver instanceof BeanFactoryAware) {
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                    ((BeanFactoryAware) autowireCandidateResolver).setBeanFactory(this);
                    return null;
                }, getAccessControlContext());
            }
            else {
                ((BeanFactoryAware) autowireCandidateResolver).setBeanFactory(this);
            }
        }
        this.autowireCandidateResolver = autowireCandidateResolver;
    }
    public void setDependencyComparator(@Nullable Comparator<Object> dependencyComparator) {
        this.dependencyComparator = dependencyComparator;
    }
    public AutowireCandidateResolver getAutowireCandidateResolver() {
        return this.autowireCandidateResolver;
    }
    @Nullable
    public Comparator<Object> getDependencyComparator() {
        return this.dependencyComparator;
    }

    @Override
    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    @Override
    public boolean isBeanNameInUse(String beanName) {
        return false;
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        return new String[0];
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return false;
    }
    /** Map from serialized id to factory instance. */
    private static final Map<String, Reference<DefaultListableBeanFactory>> serializableFactories =
            new ConcurrentHashMap<>(8);
    /**
     * List of bean definition names, in registration order.
     */
    private volatile List<String> beanDefinitionNames = new ArrayList<>(256);
    private String serializationId;
    public void setSerializationId(@Nullable String serializationId) {
        if (serializationId != null) {
            serializableFactories.put(serializationId, new WeakReference<>(this));
        }
        else if (this.serializationId != null) {
            serializableFactories.remove(this.serializationId);
        }
        this.serializationId = serializationId;
    }
    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
            throws RuntimeException {

        Assert.hasText(beanName, "Bean name must not be empty");
        Assert.notNull(beanDefinition, "BeanDefinition must not be null");

        if (beanDefinition instanceof AbstractBeanDefinition) {
            try {
                ((AbstractBeanDefinition) beanDefinition).validate();
            } catch (RuntimeException ex) {
                throw new RuntimeException(beanDefinition.getResourceDescription() + beanName +
                        "Validation of bean definition failed", ex);
            }
        }

        BeanDefinition existingDefinition = this.beanDefinitionMap.get(beanName);
        if (existingDefinition != null) {
            if (!isAllowBeanDefinitionOverriding()) {
                throw new RuntimeException("--------------error");
            } else if (existingDefinition.getRole() < beanDefinition.getRole()) {
                // e.g. was ROLE_APPLICATION, now overriding with ROLE_SUPPORT or ROLE_INFRASTRUCTURE
                if (logger.isInfoEnabled()) {
                    logger.info("Overriding user-defined bean definition for bean '" + beanName +
                            "' with a framework-generated bean definition: replacing [" +
                            existingDefinition + "] with [" + beanDefinition + "]");
                }
            } else if (!beanDefinition.equals(existingDefinition)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Overriding bean definition for bean '" + beanName +
                            "' with a different definition: replacing [" + existingDefinition +
                            "] with [" + beanDefinition + "]");
                }
            } else {
                if (logger.isTraceEnabled()) {
                    logger.trace("Overriding bean definition for bean '" + beanName +
                            "' with an equivalent definition: replacing [" + existingDefinition +
                            "] with [" + beanDefinition + "]");
                }
            }
            this.beanDefinitionMap.put(beanName, beanDefinition);
        } else {
            if (hasBeanCreationStarted()) {
                // Cannot modify startup-time collection elements anymore (for stable iteration)
                synchronized (this.beanDefinitionMap) {
                    this.beanDefinitionMap.put(beanName, beanDefinition);
                    List<String> updatedDefinitions = new ArrayList<>(this.beanDefinitionNames.size() + 1);
                    updatedDefinitions.addAll(this.beanDefinitionNames);
                    updatedDefinitions.add(beanName);
                    this.beanDefinitionNames = updatedDefinitions;
                    removeManualSingletonName(beanName);
                }
            } else {
                // Still in startup registration phase
                this.beanDefinitionMap.put(beanName, beanDefinition);
                this.beanDefinitionNames.add(beanName);
                removeManualSingletonName(beanName);
            }
            this.frozenBeanDefinitionNames = null;
        }

        if (existingDefinition != null || containsSingleton(beanName)) {
            resetBeanDefinition(beanName);
        } else if (isConfigurationFrozen()) {
            clearByTypeCache();
        }
    }

    private volatile String[] frozenBeanDefinitionNames;

    protected void resetBeanDefinition(String beanName) {
        // Remove the merged bean definition for the given bean, if already created.
        clearMergedBeanDefinition(beanName);

        // Remove corresponding bean from singleton cache, if any. Shouldn't usually
        // be necessary, rather just meant for overriding a context's default beans
        // (e.g. the default StaticMessageSource in a StaticApplicationContext).
        destroySingleton(beanName);

        // Notify all post-processors that the specified bean definition has been reset.
//        for (BeanPostProcessor processor : getBeanPostProcessors()) {
//            if (processor instanceof MergedBeanDefinitionPostProcessor) {
//                ((MergedBeanDefinitionPostProcessor) processor).resetBeanDefinition(beanName);
//            }
//        }

        // Reset all bean definitions that have the given bean as parent (recursively).
        for (String bdName : this.beanDefinitionNames) {
            if (!beanName.equals(bdName)) {
                BeanDefinition bd = this.beanDefinitionMap.get(bdName);
                // Ensure bd is non-null due to potential concurrent modification of beanDefinitionMap.
                if (bd != null && beanName.equals(bd.getParentName())) {
                    resetBeanDefinition(bdName);
                }
            }
        }
    }

    @Override
    public boolean isConfigurationFrozen() {
        return this.configurationFrozen;
    }

    @Override
    public void destroySingletons() {
        super.destroySingletons();
        updateManualSingletonNames(Set::clear, set -> !set.isEmpty());
        clearByTypeCache();
    }

    /**
     * Whether bean definition metadata may be cached for all beans.
     */
    private volatile boolean configurationFrozen = false;
    private final Map<Class<?>, String[]> allBeanNamesByType = new ConcurrentHashMap<>(64);
    private final Map<Class<?>, String[]> singletonBeanNamesByType = new ConcurrentHashMap<>(64);

    private void clearByTypeCache() {
        this.allBeanNamesByType.clear();
        this.singletonBeanNamesByType.clear();
    }

    private boolean allowBeanDefinitionOverriding = true;

    public boolean isAllowBeanDefinitionOverriding() {
        return this.allowBeanDefinitionOverriding;
    }

    private void updateManualSingletonNames(Consumer<Set<String>> action, Predicate<Set<String>> condition) {
        if (hasBeanCreationStarted()) {
            // Cannot modify startup-time collection elements anymore (for stable iteration)
            synchronized (this.beanDefinitionMap) {
                if (condition.test(this.manualSingletonNames)) {
                    Set<String> updatedSingletons = new LinkedHashSet<>(this.manualSingletonNames);
                    action.accept(updatedSingletons);
                    this.manualSingletonNames = updatedSingletons;
                }
            }
        } else {
            // Still in startup registration phase
            if (condition.test(this.manualSingletonNames)) {
                action.accept(this.manualSingletonNames);
            }
        }
    }

    /**
     * List of names of manually registered singletons, in registration order.
     */
    private volatile Set<String> manualSingletonNames = new LinkedHashSet<>(16);

    private void removeManualSingletonName(String beanName) {
        updateManualSingletonNames(set -> set.remove(beanName), set -> set.contains(beanName));
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws RuntimeException {
        BeanDefinition bd = this.beanDefinitionMap.get(beanName);
        if (bd == null) {
            if (logger.isTraceEnabled()) {
                logger.trace("No bean named '" + beanName + "' found in " + this);
            }
            throw new RuntimeException(beanName);
        }
        return bd;
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
    public Object getBean(String name, Object... args) throws RuntimeException {
        return null;
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws Exception {
        return false;
    }

    @Override
    public void registerAlias(String name, String alias) {

    }

    @Override
    public void preInstantiateSingletons() throws Exception {
        if (logger.isTraceEnabled()) {
            logger.trace("Pre-instantiating singletons in " + this);
        }

        // Iterate over a copy to allow for init methods which in turn register new bean definitions.
        // While this may not be part of the regular factory bootstrap, it does otherwise work fine.
        // 复制一份本地的所有 beanNames 集合
        List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);

        // Trigger initialization of all non-lazy singleton beans...
        // 遍历所有的 beanName
        for (String beanName : beanNames) {
            // 从容器中获取 beanName 相应的 RootBeanDefinition 对象
            RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
            // 如果该 Bean 的定义为：不是抽象、单例模式、不是懒加载方式，则进行初始化
            if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
                // 如果是 FactoryBean 类型的 Bean
                if (isFactoryBean(beanName)) {
                    // 初始化 FactoryBean 类型本身这个 Bean，注意这里在 beanName 的前面添加了一个 '&'
                    Object bean = getBean(FACTORY_BEAN_PREFIX + beanName);
                    // 如果这个 FactoryBean 为 SmartFactoryBean 类型，并且需要提前初始化
                    // 则初始 beanName 对应的 Bean，也就是调用 FactoryBean 的 getObject() 方法
                    if (bean instanceof FactoryBean) {
                        final FactoryBean<?> factory = (FactoryBean<?>) bean;
                        boolean isEagerInit;
                        if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
                            isEagerInit = AccessController.doPrivileged((PrivilegedAction<Boolean>)
                                            ((SmartFactoryBean<?>) factory)::isEagerInit,
                                    getAccessControlContext());
                        } else {
                            isEagerInit = (factory instanceof SmartFactoryBean && ((SmartFactoryBean<?>) factory).isEagerInit());
                        }
                        if (isEagerInit) {
                            getBean(beanName);
                        }
                    }
                } else {
                    // 初始化 beanName 对应的 Bean
                    getBean(beanName);
                }
            }
        }

        // Trigger post-initialization callback for all applicable beans...
        for (String beanName : beanNames) {
            Object singletonInstance = getSingleton(beanName);
            if (singletonInstance instanceof SmartInitializingSingleton) {
                final SmartInitializingSingleton smartSingleton = (SmartInitializingSingleton) singletonInstance;
                if (System.getSecurityManager() != null) {
                    AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                        smartSingleton.afterSingletonsInstantiated();
                        return null;
                    }, getAccessControlContext());
                } else {
                    smartSingleton.afterSingletonsInstantiated();
                }
            }
        }
    }

    @Override
    public void ignoreDependencyInterface(Class<?> ifc) {

    }

    @Override
    public void registerResolvableDependency(Class<?> dependencyType, Object autowiredValue) {

    }

    @Override
    public void freezeConfiguration() {

    }

    @Override
    public void clearMetadataCache() {

    }


}
