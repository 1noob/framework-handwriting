package com.springframework.beans.factory;


/**
 * @author Gary
 */
public interface BeanFactoryAware extends Aware {
    void setBeanFactory(BeanFactory beanFactory) throws RuntimeException;
}
