package com.springframework.beans.factory;

import com.springframework.core.ResolvableType;
import com.sun.istack.internal.Nullable;

/**
 * @author Gary
 */
public interface BeanFactory {
    String FACTORY_BEAN_PREFIX = "&";
    Object getBean(String name, Object... args) throws Exception;
    Object getBean(String name) throws Exception;
    <T> T getBean(Class<T> requiredType) throws RuntimeException;
    <T> T getBean(String name, Class<T> requiredType) throws Exception;
    boolean containsBean(String name);
    @Nullable
    Class<?> getType(String name, boolean allowFactoryBeanInit) throws RuntimeException;
    @Nullable
    Class<?> getType(String name) throws RuntimeException;
    boolean isTypeMatch(String name, Class<?> typeToMatch) throws Exception;

    Object getBean(Class<?> returnType, Object[] argsToUse);
}
