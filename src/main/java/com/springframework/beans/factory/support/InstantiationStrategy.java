package com.springframework.beans.factory.support;

import com.springframework.beans.factory.BeanFactory;
import com.sun.istack.internal.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public  interface InstantiationStrategy {

    Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner)
            throws RuntimeException;


    Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner,
                       Constructor<?> ctor, Object... args) throws RuntimeException;


    Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner,
                       @Nullable Object factoryBean, Method factoryMethod, Object... args)
            throws RuntimeException;

}
