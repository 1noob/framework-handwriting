package com.springframework.context.event;

import com.springframework.beans.factory.SmartInitializingSingleton;
import com.springframework.beans.factory.config.BeanFactoryPostProcessor;
import com.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.springframework.context.ApplicationContext;
import com.springframework.context.ApplicationContextAware;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class EventListenerMethodProcessor
        implements SmartInitializingSingleton, ApplicationContextAware, BeanFactoryPostProcessor {
    @Override
    public void afterSingletonsInstantiated() {

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws Exception {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws Exception {

    }
}
