package com.springframework.beans.factory.support;

import com.springframework.beans.factory.BeanFactory;
import com.springframework.beans.factory.BeanFactoryAware;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class GenericTypeAwareAutowireCandidateResolver extends SimpleAutowireCandidateResolver
        implements BeanFactoryAware {
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws RuntimeException {

    }
}
