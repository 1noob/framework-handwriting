package com.springframework.beans;

import com.sun.istack.internal.Nullable;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface BeanInfoFactory {

    /**
     * Return the bean info for the given class, if supported.
     * @param beanClass the bean class
     * @return the BeanInfo, or {@code null} if the given class is not supported
     * @throws IntrospectionException in case of exceptions
     */
    @Nullable
    BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException;

}
