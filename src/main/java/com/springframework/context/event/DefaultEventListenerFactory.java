package com.springframework.context.event;

import com.springframework.context.ApplicationListener;
import com.springframework.core.Ordered;

import java.lang.reflect.Method;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class DefaultEventListenerFactory implements EventListenerFactory, Ordered {

    private int order = LOWEST_PRECEDENCE;


    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }


    @Override
    public boolean supportsMethod(Method method) {
        return true;
    }

    @Override
    public ApplicationListener<?> createApplicationListener(String beanName, Class<?> type, Method method) {
        return new ApplicationListenerMethodAdapter(beanName, type, method);
    }

}
