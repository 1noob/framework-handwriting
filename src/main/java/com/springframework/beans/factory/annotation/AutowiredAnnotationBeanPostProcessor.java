package com.springframework.beans.factory.annotation;

import com.springframework.beans.factory.BeanFactory;
import com.springframework.beans.factory.BeanFactoryAware;
import com.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import com.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import com.springframework.core.PriorityOrdered;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class AutowiredAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
        implements MergedBeanDefinitionPostProcessor, PriorityOrdered, BeanFactoryAware {
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws RuntimeException {

    }

    @Override
    public int getOrder() {
        return 0;
    }
}
