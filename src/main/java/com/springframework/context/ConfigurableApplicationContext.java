package com.springframework.context;

import com.springframework.beans.factory.config.BeanFactoryPostProcessor;
import com.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.springframework.core.env.ConfigurableEnvironment;
import com.sun.istack.internal.Nullable;

/**
 * @author Gary
 */
public interface ConfigurableApplicationContext extends ApplicationContext, Lifecycle {
    void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor);

    void addApplicationListener(ApplicationListener<?> listener);

    void registerShutdownHook();

    void setParent(@Nullable ApplicationContext parent);

    @Override
    ConfigurableEnvironment getEnvironment();

    void refresh() throws Exception, IllegalStateException;

    ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

}

