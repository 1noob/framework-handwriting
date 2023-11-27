package com.springframework.beans;

/**
 * @author Gary
 */
public interface PropertyValues extends Iterable<PropertyValue> {
    PropertyValue[] getPropertyValues();
    boolean isEmpty();
    PropertyValue getPropertyValue(String propertyName);
    boolean contains(String propertyName);
}
