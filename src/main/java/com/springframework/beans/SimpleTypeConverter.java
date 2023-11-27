package com.springframework.beans;

import com.springframework.core.MethodParameter;
import com.springframework.core.convert.TypeDescriptor;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class SimpleTypeConverter extends TypeConverterSupport {

    @Override
    public <T> T convertIfNecessary(Object value, Class<T> requiredType) throws RuntimeException {
        return null;
    }

    @Override
    public <T> T convertIfNecessary(Object value, Class<T> requiredType, MethodParameter methodParam) throws RuntimeException {
        return null;
    }

    @Override
    public TypeDescriptor getPropertyTypeDescriptor(String propertyName) throws RuntimeException {
        return null;
    }
}
