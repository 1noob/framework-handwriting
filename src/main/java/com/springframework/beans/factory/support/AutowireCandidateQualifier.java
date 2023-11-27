package com.springframework.beans.factory.support;

import com.springframework.beans.BeanMetadataAttributeAccessor;
import com.springframework.util.Assert;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class AutowireCandidateQualifier extends BeanMetadataAttributeAccessor {
    /**
     * The name of the key used to store the value.
     */
    public static final String VALUE_KEY = "value";

    private final String typeName;

    public AutowireCandidateQualifier(String typeName) {
        Assert.notNull(typeName, "Type name must not be null");
        this.typeName = typeName;
    }
    public AutowireCandidateQualifier(Class<?> type) {
        this(type.getName());
    }
    public String getTypeName() {
        return this.typeName;
    }
}
