package com.springframework.context.annotation;

import com.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.springframework.core.env.Environment;
import com.springframework.core.io.ResourceLoader;
import com.sun.istack.internal.Nullable;

public interface ConditionContext {

    BeanDefinitionRegistry getRegistry();

    /**
     * Return the {@link ConfigurableListableBeanFactory} that will hold the bean
     * definition should the condition match, or {@code null} if the bean factory is
     * not available (or not downcastable to {@code ConfigurableListableBeanFactory}).
     */
    @Nullable
    ConfigurableListableBeanFactory getBeanFactory();

    /**
     * Return the {@link Environment} for which the current application is running.
     */
    Environment getEnvironment();

    /**
     * Return the {@link ResourceLoader} currently being used.
     */
    ResourceLoader getResourceLoader();


    @Nullable
    ClassLoader getClassLoader();
}
