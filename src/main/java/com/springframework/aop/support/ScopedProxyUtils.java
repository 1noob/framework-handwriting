package com.springframework.aop.support;

import com.springframework.aop.framework.autoproxy.AutoProxyUtils;
import com.springframework.aop.scope.ScopedProxyFactoryBean;
import com.springframework.beans.config.BeanDefinition;
import com.springframework.beans.factory.config.BeanDefinitionHolder;
import com.springframework.beans.factory.support.AbstractBeanDefinition;
import com.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.springframework.beans.factory.support.RootBeanDefinition;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class ScopedProxyUtils {
    private static final String TARGET_NAME_PREFIX = "scopedTarget.";

    private static final int TARGET_NAME_PREFIX_LENGTH = TARGET_NAME_PREFIX.length();
    public static String getTargetBeanName(String originalBeanName) {
        return TARGET_NAME_PREFIX + originalBeanName;
    }
    public static BeanDefinitionHolder createScopedProxy(BeanDefinitionHolder definition,
                                                         BeanDefinitionRegistry registry, boolean proxyTargetClass) {

        String originalBeanName = definition.getBeanName();
        BeanDefinition targetDefinition = definition.getBeanDefinition();
        String targetBeanName = getTargetBeanName(originalBeanName);

        // Create a scoped proxy definition for the original bean name,
        // "hiding" the target bean in an internal target definition.
        RootBeanDefinition proxyDefinition = new RootBeanDefinition(ScopedProxyFactoryBean.class);
        proxyDefinition.setDecoratedDefinition(new BeanDefinitionHolder(targetDefinition, targetBeanName));
        proxyDefinition.setOriginatingBeanDefinition(targetDefinition);
        proxyDefinition.setSource(definition.getSource());
        proxyDefinition.setRole(targetDefinition.getRole());

        proxyDefinition.getPropertyValues().add("targetBeanName", targetBeanName);
        if (proxyTargetClass) {
            targetDefinition.setAttribute(AutoProxyUtils.PRESERVE_TARGET_CLASS_ATTRIBUTE, Boolean.TRUE);
            // ScopedProxyFactoryBean's "proxyTargetClass" default is TRUE, so we don't need to set it explicitly here.
        }
        else {
            proxyDefinition.getPropertyValues().add("proxyTargetClass", Boolean.FALSE);
        }

        // Copy autowire settings from original bean definition.
        proxyDefinition.setAutowireCandidate(targetDefinition.isAutowireCandidate());
        proxyDefinition.setPrimary(targetDefinition.isPrimary());
        if (targetDefinition instanceof AbstractBeanDefinition) {
            proxyDefinition.copyQualifiersFrom((AbstractBeanDefinition) targetDefinition);
        }

        // The target bean should be ignored in favor of the scoped proxy.
        targetDefinition.setAutowireCandidate(false);
        targetDefinition.setPrimary(false);

        // Register the target bean as separate bean in the factory.
        registry.registerBeanDefinition(targetBeanName, targetDefinition);

        // Return the scoped proxy definition as primary bean definition
        // (potentially an inner bean).
        return new BeanDefinitionHolder(proxyDefinition, originalBeanName, definition.getAliases());
    }

}
