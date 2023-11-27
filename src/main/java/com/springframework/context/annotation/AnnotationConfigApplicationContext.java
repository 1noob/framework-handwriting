package com.springframework.context.annotation;

import com.springframework.beans.factory.support.DefaultListableBeanFactory;
import com.springframework.context.support.GenericApplicationContext;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class AnnotationConfigApplicationContext extends GenericApplicationContext implements AnnotationConfigRegistry {


    public AnnotationConfigApplicationContext(DefaultListableBeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override
    public void register(Class<?>... componentClasses) {

    }

    @Override
    public void scan(String... basePackages) {

    }
}
