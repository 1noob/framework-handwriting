package com.springframework.context.support;

import com.springframework.beans.factory.support.DefaultListableBeanFactory;
import com.springframework.web.context.ConfigurableWebApplicationContext;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class GenericWebApplicationContext extends GenericApplicationContext implements ConfigurableWebApplicationContext {

    public GenericWebApplicationContext(DefaultListableBeanFactory beanFactory) {
        super(beanFactory);
    }
}
