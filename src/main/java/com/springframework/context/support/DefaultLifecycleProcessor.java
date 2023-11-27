package com.springframework.context.support;

import com.springframework.beans.factory.BeanFactory;
import com.springframework.beans.factory.BeanFactoryAware;
import com.springframework.context.LifecycleProcessor;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class DefaultLifecycleProcessor implements LifecycleProcessor, BeanFactoryAware {
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws RuntimeException {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onClose() {

    }
}
