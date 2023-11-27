package com.springframework.beans.factory.config;

import com.sun.istack.internal.Nullable;

/**
 * @author Gary
 */
public interface SingletonBeanRegistry {
    @Nullable
    Object getSingleton(String beanName);
    boolean containsSingleton(String beanName);
    Object getSingletonMutex();
    void registerSingleton(String beanName, Object singletonObject);

}
