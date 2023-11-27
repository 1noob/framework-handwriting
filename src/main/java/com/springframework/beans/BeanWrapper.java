package com.springframework.beans;

import com.springframework.core.convert.ConversionService;

import java.beans.PropertyDescriptor;
import java.util.Map;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface BeanWrapper extends ConfigurablePropertyAccessor {
    Object getWrappedInstance();
    Class<?> getWrappedClass();
    PropertyDescriptor[] getPropertyDescriptors();
    PropertyDescriptor getPropertyDescriptor(String propertyName) throws RuntimeException;
}
