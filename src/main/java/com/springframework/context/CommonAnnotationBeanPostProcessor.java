package com.springframework.context;

import com.springframework.beans.factory.BeanFactory;
import com.springframework.beans.factory.BeanFactoryAware;
import com.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor;
import com.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;

import java.io.Serializable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class CommonAnnotationBeanPostProcessor extends InitDestroyAnnotationBeanPostProcessor
        implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws RuntimeException {

    }
}
