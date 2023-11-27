package com.springframework.beans;

import com.springframework.core.AttributeAccessor;
import com.springframework.util.Assert;
import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class PropertyValue extends BeanMetadataAttributeAccessor {
    private final String name;
    public boolean isConverted() {
        return this.converted;
    }
    public synchronized void setConvertedValue(@Nullable Object value) {
        this.converted = true;
        this.convertedValue = value;
    }
    public PropertyValue getOriginalPropertyValue() {
        PropertyValue original = this;
        Object source = getSource();
        while (source instanceof PropertyValue && source != original) {
            original = (PropertyValue) source;
            source = original.getSource();
        }
        return original;
    }
    public synchronized Object getConvertedValue() {
        return this.convertedValue;
    }
    private final Object value;

    private boolean optional = false;

    private boolean converted = false;

    private Object convertedValue;

    /**
     * Package-visible field that indicates whether conversion is necessary.
     */
    volatile Boolean conversionNecessary;

    /**
     * Package-visible field for caching the resolved property path tokens.
     */
    transient volatile Object resolvedTokens;
    private Object source;


    public PropertyValue(PropertyValue original) {
        Assert.notNull(original, "Original must not be null");
        this.name = original.getName();
        this.value = original.getValue();
        this.optional = original.isOptional();
        this.converted = original.converted;
        this.convertedValue = original.convertedValue;
        this.conversionNecessary = original.conversionNecessary;
        this.resolvedTokens = original.resolvedTokens;
        setSource(original.getSource());
        copyAttributesFrom(original);
    }

    public PropertyValue(String name, Object value) {
        Assert.notNull(name, "Name must not be null");
        this.name = name;
        this.value = value;
    }
    public PropertyValue(PropertyValue original, @Nullable Object newValue) {
        Assert.notNull(original, "Original must not be null");
        this.name = original.getName();
        this.value = newValue;
        this.optional = original.isOptional();
        this.conversionNecessary = original.conversionNecessary;
        this.resolvedTokens = original.resolvedTokens;
        setSource(original);
        copyAttributesFrom(original);
    }


    @Override
    public void setSource(Object source) {
        this.source = source;
    }

    public Object getValue() {
        return this.value;
    }

    public boolean isOptional() {
        return this.optional;
    }

    /**
     * Return the name of the property.
     */
    public String getName() {
        return this.name;
    }
}
