package com.springframework.beans.factory.support;

import com.springframework.beans.MutablePropertyValues;
import com.springframework.beans.config.BeanDefinition;

/**
 * @Author 虎哥
 * @Description XML 定义 Bean
 * 多了一个 parentName，表示有继承关系，是一个标准 Bean 元信息对象，通过 XML 定义的 Bean 会解析成该对象
 * <p>
 * 要带着问题去学习,多猜想多验证
 **/
public class GenericBeanDefinition extends AbstractBeanDefinition {

    protected GenericBeanDefinition(BeanDefinition original) {
        super(original);
    }

    public GenericBeanDefinition() {
        super();
    }

    @Override
    public AbstractBeanDefinition cloneBeanDefinition() {
        return null;
    }

    @Override
    public String getParentName() {
        return null;
    }

    @Override
    public String getResourceDescription() {
        return null;
    }

    @Override
    public boolean isLazyInit() {
        return false;
    }

    @Override
    public BeanDefinition getOriginatingBeanDefinition() {
        return null;
    }

    @Override
    public boolean isPrimary() {
        return false;
    }

    @Override
    public boolean isAutowireCandidate() {
        return false;
    }

    @Override
    public void setParentName(String parentName) {

    }

    @Override
    public String getScope() {
        return null;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public String getFactoryBeanName() {
        return null;
    }

    @Override
    public String getFactoryMethodName() {
        return null;
    }

    @Override
    public int getRole() {
        return 0;
    }

}
