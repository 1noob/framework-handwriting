package com.springframework.context.event;

import com.springframework.context.ApplicationListener;

import java.lang.reflect.Method;

public interface EventListenerFactory {
    boolean supportsMethod(Method method);

    ApplicationListener<?> createApplicationListener(String beanName, Class<?> type, Method method);

}
