package com.springframework.beans.factory.support;

import com.springframework.beans.factory.FactoryBean;
import com.sun.istack.internal.Nullable;

import java.security.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class FactoryBeanRegistrySupport extends DefaultSingletonBeanRegistry {
    /** Cache of singleton objects created by FactoryBeans: FactoryBean name to object. */
    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>(16);
    @Nullable
    protected Class<?> getTypeForFactoryBean(FactoryBean<?> factoryBean) {
        try {
            if (System.getSecurityManager() != null) {
                return AccessController.doPrivileged(
                        (PrivilegedAction<Class<?>>) factoryBean::getObjectType, getAccessControlContext());
            }
            else {
                return factoryBean.getObjectType();
            }
        }
        catch (Throwable ex) {
            // Thrown from the FactoryBean's getObjectType implementation.
//            log.info("FactoryBean threw exception from getObjectType, despite the contract saying " +
//                    "that it should return null if the type of its object cannot be determined yet", ex);
            return null;
        }
    }
    @Nullable
    protected Object getCachedObjectForFactoryBean(String beanName) {
        return this.factoryBeanObjectCache.get(beanName);
    }
    protected Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName, boolean shouldPostProcess) {
        // <1> `factory` 为单例模式，且单例 Bean 缓存中存在 `beanName` 对应的 FactoryBean 对象
        if (factory.isSingleton() && containsSingleton(beanName)) {
            synchronized (getSingletonMutex()) {// <1.1> 获取单例锁，保证安全
                // <1.2> 从 `factoryBeanObjectCache` 缓存中获取 FactoryBean#getObject() 创建的目标对象
                Object object = this.factoryBeanObjectCache.get(beanName);
                if (object == null) {
                    // <1.3> 则根据 `factory` 获取目标对象，调用 FactoryBean#getObject() 方法
                    object = doGetObjectFromFactoryBean(factory, beanName);
                    // Only post-process and store if not put there already during getObject() call above
                    // (e.g. because of circular reference processing triggered by custom getBean calls)
                    // <1.4> 这里再进行一次校验，看是否在缓存中存在 FactoryBean 创建的目标对象，如果有则优先从缓存中获取
                    // 保证 FactoryBean#getObject() 只能被调用一次
                    // 没有的话，则对刚获取到的目标对象进行接下来的处理
                    Object alreadyThere = this.factoryBeanObjectCache.get(beanName);
                    if (alreadyThere != null) {
                        object = alreadyThere;
                    }
                    else {
                        // <1.5> 是否需要后续处理，这个 FactoryBean 的前身 BeanDefinition 是否由 Spring 解析出来的，通常情况下都是
                        if (shouldPostProcess) {
                            // <1.5.1> 若该 FactoryBean 处于创建中，则直接返回这个目标对象，不进行接下来的处理过程
                            if (isSingletonCurrentlyInCreation(beanName)) {
                                // Temporarily return non-post-processed object, not storing it yet..
                                return object;
                            }
                            // <1.5.2> 前置处理，将 `beanName` 标志为正在创建
                            beforeSingletonCreation(beanName);
                            try {
                                // <1.5.3> 对通过 FactoryBean 获取的目标对象进行后置处理
                                // 遍历所有的 BeanPostProcessor 的 postProcessAfterInitialization 方法（初始化的处理）
                                object = postProcessObjectFromFactoryBean(object, beanName);
                            }
                            catch (Throwable ex) {
                                throw new RuntimeException(beanName+
                                        "Post-processing of FactoryBean's singleton object failed", ex);
                            }
                            finally {
                                // <1.5.4> 后置处理，将 `beanName` 标志为不在创建中
                                afterSingletonCreation(beanName);
                            }
                        }
                        // <1.6> 如果缓存中存在 `beanName` 对应的 FactoryBean 对象
                        // 上面不是判断了吗？也可能在上面的处理过程会有所变化，所以这里在做一层判断
                        // 目的：缓存 FactoryBean 创建的目标对象，则需要保证 FactoryBean 本身这个对象存在缓存中
                        if (containsSingleton(beanName)) {
                            // <1.6.1> 将这个 FactoryBean 创建的目标对象保存至 `factoryBeanObjectCache`
                            this.factoryBeanObjectCache.put(beanName, object);
                        }
                    }
                }
                // <1.7> 返回 FactoryBean 创建的目标对象
                return object;
            }
        }
        // <2> `factory` 非单例模式，或单例 Bean 缓存中不存在 `beanName` 对应的 FactoryBean 对象
        else {
            // <2.1> 则根据 `factory` 获取目标对象，调用 FactoryBean#getObject() 方法
            Object object = doGetObjectFromFactoryBean(factory, beanName);
            // <2.2> 是否需要后续处理，这个 FactoryBean 的前身 BeanDefinition 是否由 Spring 解析出来的，通常情况下都是
            if (shouldPostProcess) {
                try {
                    // <2.2.1> 对通过 FactoryBean 获取的目标对象进行后置处理
                    // 遍历所有的 BeanPostProcessor 的 postProcessAfterInitialization 方法（初始化的处理）
                    object = postProcessObjectFromFactoryBean(object, beanName);
                }
                catch (Throwable ex) {
                    throw new RuntimeException(beanName+"Post-processing of FactoryBean's object failed", ex);
                }
            }
            // <2.3> 返回 FactoryBean 创建的目标对象，非单例模式不会进行缓存
            return object;
        }
    }
    protected Object postProcessObjectFromFactoryBean(Object object, String beanName) throws RuntimeException {
        return object;
    }

    private Object doGetObjectFromFactoryBean(FactoryBean<?> factory, String beanName) throws RuntimeException {
        Object object;
        try {
            if (System.getSecurityManager() != null) {
                AccessControlContext acc = getAccessControlContext();
                try {
                    object = AccessController.doPrivileged((PrivilegedExceptionAction<Object>) factory::getObject, acc);
                }
                catch (PrivilegedActionException pae) {
                    throw pae.getException();
                }
            }
            else {
                object = factory.getObject();
            }
        }
        catch (Exception ex) {
            throw new RuntimeException(beanName+ex.toString());
        }
        catch (Throwable ex) {
            throw new RuntimeException(beanName+ "FactoryBean threw exception on object creation", ex);
        }

        // Do not accept a null value for a FactoryBean that's not fully
        // initialized yet: Many FactoryBeans just return null then.
        if (object == null) {
            if (isSingletonCurrentlyInCreation(beanName)) {
                throw new RuntimeException(
                        beanName+"FactoryBean which is currently in creation returned null from getObject");
            }
            object = new NullBean();
        }
        return object;
    }
    protected AccessControlContext getAccessControlContext() {
        return AccessController.getContext();
    }

}
