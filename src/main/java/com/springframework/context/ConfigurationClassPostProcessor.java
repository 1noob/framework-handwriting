package com.springframework.context;

import com.springframework.beans.factory.BeanClassLoaderAware;
import com.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import com.springframework.core.PriorityOrdered;
import com.springframework.core.env.Environment;
import com.springframework.core.io.ResourceLoader;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor,
        PriorityOrdered, ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware {
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws Exception {

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws Exception {

    }

    @Override
    public void setEnvironment(Environment environment) {

    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {

    }

    @Override
    public int getOrder() {
        return 0;
    }
}
