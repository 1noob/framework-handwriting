package com.springframework.context.support;

import com.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class GenericXmlApplicationContext extends GenericApplicationContext {

    public GenericXmlApplicationContext(DefaultListableBeanFactory beanFactory) {
        super(beanFactory);
    }
}
