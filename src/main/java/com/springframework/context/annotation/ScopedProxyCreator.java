package com.springframework.context.annotation;

import com.springframework.aop.support.ScopedProxyUtils;
import com.springframework.beans.factory.config.BeanDefinitionHolder;
import com.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
final class ScopedProxyCreator {

    private ScopedProxyCreator() {
    }


    public static BeanDefinitionHolder createScopedProxy(
            BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry, boolean proxyTargetClass) {

        return ScopedProxyUtils.createScopedProxy(definitionHolder, registry, proxyTargetClass);
    }
//
//    public static String getTargetBeanName(String originalBeanName) {
//        return ScopedProxyUtils.getTargetBeanName(originalBeanName);
//    }

}
