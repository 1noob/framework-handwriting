package com.springframework.beans.factory.support;

import com.springframework.beans.config.BeanDefinition;
import com.springframework.core.AliasRegistry;

/**
 * @Author 虎哥
 * @Description 保存 Bean定义对象的注册表的接口,例如 RootBeanDefinition和 ChildBeanDefinition 实例。通常由 BeanFactories 实现
 * 内部使用 有层次结构的 AbstractBeanDefinition
 * |要带着问题去学习,多猜想多验证|
 **/
public interface BeanDefinitionRegistry extends AliasRegistry {

    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
            throws RuntimeException;

    boolean containsBeanDefinition(String beanName);
    BeanDefinition getBeanDefinition(String beanName) throws Exception;

    /**
     * Return the names of all beans defined in this registry.
     *
     * @return the names of all beans defined in this registry,
     * or an empty array if none defined
     */

    /**
     * Return the number of beans defined in the registry.
     *
     * @return the number of beans defined in the registry
     */
    int getBeanDefinitionCount();

    /**
     * Determine whether the given bean name is already in use within this registry,
     * i.e. whether there is a local bean or alias registered under this name.
     *
     * @param beanName the name to check
     * @return whether the given bean name is already in use
     */
    boolean isBeanNameInUse(String beanName);

}
