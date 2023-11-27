package com.springframework.beans.factory.support;

import com.springframework.util.Assert;
import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class ManagedArray extends ManagedList<Object> {

    /**
     * Resolved element type for runtime creation of the target array.
     */
    @Nullable
    volatile Class<?> resolvedElementType;


    /**
     * Create a new managed array placeholder.
     *
     * @param elementTypeName the target element type as a class name
     * @param size            the size of the array
     */
    public ManagedArray(String elementTypeName, int size) {
        super(size);
        Assert.notNull(elementTypeName, "elementTypeName must not be null");
        setElementTypeName(elementTypeName);
    }

}
