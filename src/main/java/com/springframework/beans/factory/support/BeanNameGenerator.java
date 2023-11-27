package com.springframework.beans.factory.support;

import com.springframework.beans.config.BeanDefinition;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface BeanNameGenerator {

    /**
     * Generate a bean name for the given bean definition.
     * @param definition the bean definition to generate a name for
     * @param registry the bean definition registry that the given definition
     * is supposed to be registered with
     * @return the generated bean name
     */
    String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry);

}
