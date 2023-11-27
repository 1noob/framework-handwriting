package com.springframework.beans.factory.support;

import com.springframework.beans.factory.config.BeanFactoryPostProcessor;

/**
 * @author Gary
 */
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {
    void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws Exception;

}
