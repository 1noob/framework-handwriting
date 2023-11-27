package com.springframework.beans.config;

import com.springframework.beans.BeanMetadataElement;
import com.springframework.beans.MutablePropertyValues;
import com.springframework.beans.factory.config.ConfigurableBeanFactory;
import com.springframework.beans.factory.config.ConstructorArgumentValues;
import com.springframework.core.AttributeAccessor;
import com.sun.istack.internal.Nullable;

/**
 * 定义一个 Bean 的元信息
 *
 * @author Gary
 */
public interface BeanDefinition extends AttributeAccessor,BeanMetadataElement {
    String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;
    String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;
    int ROLE_APPLICATION = 0;
    int ROLE_INFRASTRUCTURE = 2;
    boolean isPrototype();
    String getParentName();
    void setLazyInit(boolean lazyInit);
    String getResourceDescription();
    boolean isLazyInit();
    void setDescription(@Nullable String description);
    BeanDefinition getOriginatingBeanDefinition();
    MutablePropertyValues getPropertyValues();
    ConstructorArgumentValues getConstructorArgumentValues();
    String getDestroyMethodName();
    String getInitMethodName();
    boolean isPrimary();
    boolean isAutowireCandidate();
    String[] getDependsOn();
    void setDestroyMethodName(String destroyMethodName);
    void setInitMethodName(String initMethodName);
    void setPrimary(boolean primary);
    void setAutowireCandidate(boolean autowireCandidate);
    void setDependsOn(String... dependsOn);
    default boolean hasPropertyValues() {
        return !getPropertyValues().isEmpty();
    }
    default boolean hasConstructorArgumentValues() {
        return !getConstructorArgumentValues().isEmpty();
    }
    void setBeanClassName(String beanClassName);
    void setParentName(String parentName);
    String getBeanClassName();
    String getScope();
    void setScope(String scope);
    boolean isAbstract();
    String getFactoryBeanName();
    String getFactoryMethodName();
    int getRole();
    void setFactoryBeanName(String factoryBeanName);
    void setFactoryMethodName(String factoryMethodName);
    void setRole(int role);
    boolean isSingleton();

}
