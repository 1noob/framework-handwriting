package com.springframework.beans.factory.config;

import com.springframework.beans.factory.BeanFactory;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface AutowireCapableBeanFactory extends BeanFactory {
    int AUTOWIRE_BY_TYPE = 2;
    int AUTOWIRE_BY_NAME = 1;
    int AUTOWIRE_CONSTRUCTOR = 3;
    int AUTOWIRE_NO = 0;
    int AUTOWIRE_AUTODETECT = 4;
    Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
            throws RuntimeException;
    Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
            throws RuntimeException;
    <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws RuntimeException;
}
