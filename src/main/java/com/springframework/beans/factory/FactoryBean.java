package com.springframework.beans.factory;

import com.sun.istack.internal.Nullable;

/**
 * @author Gary
 */
public interface FactoryBean<T>  {
    String OBJECT_TYPE_ATTRIBUTE = "factoryBeanObjectType";
    default boolean isSingleton() {
        return true;
    }
    T getObject() throws Exception;
    @Nullable
    Class<?> getObjectType();
}
