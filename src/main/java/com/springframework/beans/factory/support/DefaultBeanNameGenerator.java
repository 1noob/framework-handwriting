package com.springframework.beans.factory.support;

import com.springframework.beans.config.BeanDefinition;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class DefaultBeanNameGenerator implements BeanNameGenerator {

    /**
     * A convenient constant for a default {@code DefaultBeanNameGenerator} instance,
     * as used for {@link AbstractBeanDefinitionReader} setup.
     * @since 5.2
     */
    public static final DefaultBeanNameGenerator INSTANCE = new DefaultBeanNameGenerator();


    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        return BeanDefinitionReaderUtils.generateBeanName(definition, registry);
    }

}
