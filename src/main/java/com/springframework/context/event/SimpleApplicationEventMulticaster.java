package com.springframework.context.event;

import com.springframework.beans.factory.BeanFactory;
import com.springframework.context.ApplicationEvent;
import com.springframework.context.ApplicationListener;
import com.springframework.core.ResolvableType;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster {
    /**
     * Create a new SimpleApplicationEventMulticaster for the given BeanFactory.
     */
    public SimpleApplicationEventMulticaster(BeanFactory beanFactory) {
        setBeanFactory(beanFactory);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {

    }

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {

    }

    @Override
    public void addApplicationListenerBean(String listenerBeanName) {

    }

    @Override
    public void removeApplicationListener(ApplicationListener<?> listener) {

    }

    @Override
    public void removeApplicationListenerBean(String listenerBeanName) {

    }

    @Override
    public void removeAllListeners() {

    }

    @Override
    public void multicastEvent(ApplicationEvent event) {

    }

    @Override
    public void multicastEvent(ApplicationEvent event, ResolvableType eventType) {

    }
}
