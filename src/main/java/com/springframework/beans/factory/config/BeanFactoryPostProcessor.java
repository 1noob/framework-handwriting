package com.springframework.beans.factory.config;

/**
 * @author Gary
 */
@FunctionalInterface
public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws Exception;

}
