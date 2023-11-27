package com.springframework.beans;

import com.springframework.core.MethodParameter;
import com.springframework.core.convert.TypeDescriptor;
import com.sun.istack.internal.Nullable;

public interface TypeConverter {
    <T> T convertIfNecessary(Object value, Class<T> requiredType) throws RuntimeException;
    <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType,
                             @Nullable MethodParameter methodParam) throws RuntimeException;
    TypeDescriptor getPropertyTypeDescriptor(String propertyName) throws RuntimeException;

}
