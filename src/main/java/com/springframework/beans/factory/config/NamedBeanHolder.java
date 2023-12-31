package com.springframework.beans.factory.config;

import com.springframework.beans.factory.NamedBean;
import com.springframework.util.Assert;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class NamedBeanHolder<T> implements NamedBean {

    private final String beanName;

    private final T beanInstance;


    /**
     * Create a new holder for the given bean name plus instance.
     * @param beanName the name of the bean
     * @param beanInstance the corresponding bean instance
     */
    public NamedBeanHolder(String beanName, T beanInstance) {
        Assert.notNull(beanName, "Bean name must not be null");
        this.beanName = beanName;
        this.beanInstance = beanInstance;
    }


    /**
     * Return the name of the bean.
     */
    @Override
    public String getBeanName() {
        return this.beanName;
    }

    /**
     * Return the corresponding bean instance.
     */
    public T getBeanInstance() {
        return this.beanInstance;
    }

}
