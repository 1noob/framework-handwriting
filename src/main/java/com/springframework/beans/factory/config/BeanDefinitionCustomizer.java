package com.springframework.beans.factory.config;

import com.springframework.beans.config.BeanDefinition;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
@FunctionalInterface
public interface BeanDefinitionCustomizer {

    /**
     * Customize the given bean definition.
     */
    void customize(BeanDefinition bd);

}

