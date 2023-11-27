package com.springframework.beans.factory.support;

import com.springframework.beans.factory.DisposableBean;
import com.springframework.beans.factory.ObjectFactory;
import com.springframework.beans.factory.config.SingletonBeanRegistry;
import com.springframework.core.SimpleAliasRegistry;
import com.springframework.util.Assert;
import com.springframework.util.StringUtils;
import com.sun.istack.internal.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class DefaultSingletonBeanRegistry extends SimpleAliasRegistry implements SingletonBeanRegistry {
    /**
     * Cache of singleton objects: bean name to bean instance.
     */
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    /**
     * Cache of singleton factories: bean name to ObjectFactory.
     */
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);

    /**
     * Cache of early singleton objects: bean name to bean instance.
     */
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    /**
     * Set of registered singletons, containing the bean names in registration order.
     */
    private final Set<String> registeredSingletons = new LinkedHashSet<>(256);
    /**
     * Cache of singleton factories: bean name to ObjectFactory.
     */

    private final Map<String, Object> disposableBeans = new LinkedHashMap<>();

    private final Map<String, Set<String>> dependenciesForBeanMap = new ConcurrentHashMap<>(64);
    private final Map<String, Set<String>> containedBeanMap = new ConcurrentHashMap<>(16);
    private final Set<String> inCreationCheckExclusions =
            Collections.newSetFromMap(new ConcurrentHashMap<>(16));
    private final Set<String> singletonsCurrentlyInCreation =
            Collections.newSetFromMap(new ConcurrentHashMap<>(16));
    private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap<>(64);

    protected void afterSingletonCreation(String beanName) {
        if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.remove(beanName)) {
            throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
        }
    }

    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        Assert.notNull(singletonFactory, "Singleton factory must not be null");
        synchronized (this.singletonObjects) {
            if (!this.singletonObjects.containsKey(beanName)) {
                this.singletonFactories.put(beanName, singletonFactory);
                this.earlySingletonObjects.remove(beanName);
                this.registeredSingletons.add(beanName);
            }
        }
    }
    public void registerContainedBean(String containedBeanName, String containingBeanName) {
        synchronized (this.containedBeanMap) {
            Set<String> containedBeans =
                    this.containedBeanMap.computeIfAbsent(containingBeanName, k -> new LinkedHashSet<>(8));
            if (!containedBeans.add(containedBeanName)) {
                return;
            }
        }
        registerDependentBean(containedBeanName, containingBeanName);
    }

    public void registerDisposableBean(String beanName, DisposableBean bean) {
        synchronized (this.disposableBeans) {
            this.disposableBeans.put(beanName, bean);
        }
    }

    public void registerDependentBean(String beanName, String dependentBeanName) {
        String canonicalName = canonicalName(beanName);
        // 对应关系：beanName -> 依赖 beanName 的集合
        synchronized (this.dependentBeanMap) {
            Set<String> dependentBeans =
                    this.dependentBeanMap.computeIfAbsent(canonicalName, k -> new LinkedHashSet<>(8));
            if (!dependentBeans.add(dependentBeanName)) {
                return;
            }
        }
        // 对应关系：beanName - > beanName 的依赖的集合
        synchronized (this.dependenciesForBeanMap) {
            Set<String> dependenciesForBean =
                    this.dependenciesForBeanMap.computeIfAbsent(dependentBeanName, k -> new LinkedHashSet<>(8));
            dependenciesForBean.add(canonicalName);
        }
    }

    protected void beforeSingletonCreation(String beanName) {
        if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.add(beanName)) {
            throw new RuntimeException(beanName);
        }
    }

    @Override
    public final Object getSingletonMutex() {
        return this.singletonObjects;
    }

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {

    }

    protected void removeSingleton(String beanName) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.remove(beanName);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.remove(beanName);
        }
    }

    @Nullable
    private Set<Exception> suppressedExceptions;

    /**
     * Maximum number of suppressed exceptions to preserve.
     */
    private static final int SUPPRESSED_EXCEPTIONS_LIMIT = 100;

    protected void onSuppressedException(Exception ex) {
        synchronized (this.singletonObjects) {
            if (this.suppressedExceptions != null && this.suppressedExceptions.size() < SUPPRESSED_EXCEPTIONS_LIMIT) {
                this.suppressedExceptions.add(ex);
            }
        }
    }

    public void destroySingleton(String beanName) {
        // Remove a registered singleton of the given name, if any.
        removeSingleton(beanName);

        // Destroy the corresponding DisposableBean instance.
        DisposableBean disposableBean;
        synchronized (this.disposableBeans) {
            disposableBean = (DisposableBean) this.disposableBeans.remove(beanName);
        }
        destroyBean(beanName, disposableBean);
    }

    public boolean isCurrentlyInCreation(String beanName) {
        Assert.notNull(beanName, "Bean name must not be null");
        return (!this.inCreationCheckExclusions.contains(beanName) && isActuallyInCreation(beanName));
    }

    protected boolean isActuallyInCreation(String beanName) {
        return isSingletonCurrentlyInCreation(beanName);
    }

    @Override
    public boolean containsSingleton(String beanName) {
        return this.singletonObjects.containsKey(beanName);
    }

    protected void destroyBean(String beanName, @Nullable DisposableBean bean) {
        // Trigger destruction of dependent beans first...
        Set<String> dependencies;
        synchronized (this.dependentBeanMap) {
            // Within full synchronization in order to guarantee a disconnected Set
            dependencies = this.dependentBeanMap.remove(beanName);
        }
        if (dependencies != null) {
//            if (log.isTraceEnabled()) {
//                log.trace("Retrieved dependent beans for bean '" + beanName + "': " + dependencies);
//            }
            for (String dependentBeanName : dependencies) {
                destroySingleton(dependentBeanName);
            }
        }

        // Actually destroy the bean now...
        if (bean != null) {
            try {
                bean.destroy();
            } catch (Throwable ex) {
//                if (log.isWarnEnabled()) {
//                    log.warn("Destruction of bean with name '" + beanName + "' threw an exception", ex);
//                }
            }
        }

        // Trigger destruction of contained beans...
        Set<String> containedBeans;
        synchronized (this.containedBeanMap) {
            // Within full synchronization in order to guarantee a disconnected Set
            containedBeans = this.containedBeanMap.remove(beanName);
        }
        if (containedBeans != null) {
            for (String containedBeanName : containedBeans) {
                destroySingleton(containedBeanName);
            }
        }

        // Remove destroyed bean from other beans' dependencies.
        synchronized (this.dependentBeanMap) {
            for (Iterator<Map.Entry<String, Set<String>>> it = this.dependentBeanMap.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Set<String>> entry = it.next();
                Set<String> dependenciesToClean = entry.getValue();
                dependenciesToClean.remove(beanName);
                if (dependenciesToClean.isEmpty()) {
                    it.remove();
                }
            }
        }

        // Remove destroyed bean's prepared dependency information.
        this.dependenciesForBeanMap.remove(beanName);
    }

    @Override
    @Nullable
    public Object getSingleton(String beanName) {
        return getSingleton(beanName, true);
    }

    public boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }

    protected boolean isDependent(String beanName, String dependentBeanName) {
        synchronized (this.dependentBeanMap) {
            return isDependent(beanName, dependentBeanName, null);
        }
    }
    protected boolean hasDependentBean(String beanName) {
        return this.dependentBeanMap.containsKey(beanName);
    }
    public String[] getDependentBeans(String beanName) {
        Set<String> dependentBeans = this.dependentBeanMap.get(beanName);
        if (dependentBeans == null) {
            return new String[0];
        }
        synchronized (this.dependentBeanMap) {
            return StringUtils.toStringArray(dependentBeans);
        }
    }
    private boolean isDependent(String beanName, String dependentBeanName, @Nullable Set<String> alreadySeen) {
        // <1> `alreadySeen` 中已经检测过该 `beanName` 则直接返回 `false`
        if (alreadySeen != null && alreadySeen.contains(beanName)) {
            return false;
        }
        // <2> 获取最终的 `beanName`，因为可能是别名，需要进行相关处理
        String canonicalName = canonicalName(beanName);
        // <3> 从 `dependentBeanMap` 中获取依赖 `beanName` 的 Bean 集合
        Set<String> dependentBeans = this.dependentBeanMap.get(canonicalName);
        // <4> 没有 Bean 依赖该 `beanName`，也就不存在循环依赖，返回 `false`
        if (dependentBeans == null) {
            return false;
        }
        // <5> 依赖 `beanName` 的 Bean 们包含 `dependentBeanName`，表示出现循环依赖，返回 `true`
        if (dependentBeans.contains(dependentBeanName)) {
            // `beanName` 与 `dependentBeanName` 相互依赖
            return true;
        }
        // <6> 对依赖该 `beanName` 的 Bean 们进行检查，看它们是否与 `dependentBeanName` 存在依赖，递归处理
        for (String transitiveDependency : dependentBeans) {
            if (alreadySeen == null) {
                alreadySeen = new HashSet<>();
            }
            alreadySeen.add(beanName);
            if (isDependent(transitiveDependency, dependentBeanName, alreadySeen)) {
                return true;
            }
        }
        return false;
    }

    private boolean singletonsCurrentlyInDestruction = false;

    public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
        Assert.notNull(beanName, "Bean name must not be null");
        // 全局加锁
        synchronized (this.singletonObjects) {
            // <1> 从 `singletonObjects` 单例 Bean 的缓存中获取 Bean（再检查一遍），存在则直接返回，否则开始创建
            Object singletonObject = this.singletonObjects.get(beanName);
            if (singletonObject == null) {
                if (this.singletonsCurrentlyInDestruction) {
                    throw new RuntimeException(beanName+
                            "Singleton bean creation not allowed while singletons of this factory are in destruction " +
                                    "(Do not request a bean from a BeanFactory in a destroy method implementation!)");
                }
//                if (log.isDebugEnabled()) {
//                    log.debug("Creating shared instance of singleton bean '" + beanName + "'");
//                }
                // <2> 将 `beanName` 标记为单例模式正在创建
                beforeSingletonCreation(beanName);
                boolean newSingleton = false;
                boolean recordSuppressedExceptions = (this.suppressedExceptions == null);
                if (recordSuppressedExceptions) {
                    this.suppressedExceptions = new LinkedHashSet<>();
                }
                try {
                    /**
                     * <3> 创建 Bean，实际调用
                     * {@link AbstractAutowireCapableBeanFactory#createBean(String, RootBeanDefinition, Object[])} 方法
                     */
                    singletonObject = singletonFactory.getObject();
                    newSingleton = true;
                } catch (IllegalStateException ex) {
                    // Has the singleton object implicitly appeared in the meantime ->
                    // if yes, proceed with it since the exception indicates that state.
                    singletonObject = this.singletonObjects.get(beanName);
                    if (singletonObject == null) {
                        throw ex;
                    }
                } catch (Exception ex) {
                    if (recordSuppressedExceptions) {
                        for (Exception suppressedException : this.suppressedExceptions) {
//                            ex.addRelatedCause(suppressedException);
                        }
                    }
                    throw ex;
                } finally {
                    if (recordSuppressedExceptions) {
                        this.suppressedExceptions = null;
                    }
                    // <4> 将 `beanName` 标记为不在创建中，照应第 `2` 步
                    afterSingletonCreation(beanName);
                }
                // <5> 如果这里是新创建的单例模式 Bean，则在 `singletonObjects` 中进行缓存（无序），移除缓存的早期对象
                // 并在 `registeredSingletons` 中保存 `beanName`，保证注册顺序
                if (newSingleton) {
                    addSingleton(beanName, singletonObject);
                }
            }
            return singletonObject;
        }
    }

    protected void addSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.put(beanName, singletonObject);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.add(beanName);
        }
    }

    @Nullable
    protected Object getSingleton(String beanName, boolean allowEarlyReference) {
        // <1> **【一级 Map】**从单例缓存 `singletonObjects` 中获取 beanName 对应的 Bean
        Object singletonObject = this.singletonObjects.get(beanName);
        // <2> 如果**一级 Map**中不存在，且当前 beanName 正在创建
        if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
            // <2.1> 对 `singletonObjects` 加锁
            synchronized (this.singletonObjects) {
                // <2.2> **【二级 Map】**从 `earlySingletonObjects` 集合中获取，里面会保存从 **三级 Map** 获取到的正在初始化的 Bean
                singletonObject = this.earlySingletonObjects.get(beanName);
                // <2.3> 如果**二级 Map** 中不存在，且允许提前创建
                if (singletonObject == null && allowEarlyReference) {
                    // <2.3.1> **【三级 Map】**从 `singletonFactories` 中获取对应的 ObjectFactory 实现类
                    ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                    // 如果从**三级 Map** 中存在对应的对象，则进行下面的处理
                    if (singletonFactory != null) {
                        // <2.3.2> 调用 ObjectFactory#getOject() 方法，获取目标 Bean 对象（早期半成品）
                        singletonObject = singletonFactory.getObject();
                        // <2.3.3> 将目标对象放入**二级 Map**
                        this.earlySingletonObjects.put(beanName, singletonObject);
                        // <2.3.4> 从**三级 Map**移除 `beanName`
                        this.singletonFactories.remove(beanName);
                    }
                }
            }
        }
        // <3> 返回从缓存中获取的对象
        return singletonObject;
    }
}
