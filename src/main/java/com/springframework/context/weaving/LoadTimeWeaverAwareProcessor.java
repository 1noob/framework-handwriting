package com.springframework.context.weaving;

import com.springframework.beans.factory.BeanFactory;
import com.springframework.beans.factory.BeanFactoryAware;
import com.springframework.beans.factory.config.BeanPostProcessor;
import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class LoadTimeWeaverAwareProcessor implements BeanPostProcessor, BeanFactoryAware {
    @Nullable
    private BeanFactory beanFactory;

    public LoadTimeWeaverAwareProcessor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
