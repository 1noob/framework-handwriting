package com.springframework.beans.factory.config;

import com.springframework.beans.config.BeanDefinition;
import com.springframework.beans.factory.ListableBeanFactory;
import com.sun.istack.internal.Nullable;

/**
 * @author Gary
 */
public interface ConfigurableListableBeanFactory extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {
    boolean isConfigurationFrozen();
    void preInstantiateSingletons() throws Exception;
    void ignoreDependencyInterface(Class<?> ifc);
    void registerResolvableDependency(Class<?> dependencyType, @Nullable Object autowiredValue);
    void freezeConfiguration();
    void clearMetadataCache();
    BeanDefinition getBeanDefinition(String beanName) throws Exception;

}
