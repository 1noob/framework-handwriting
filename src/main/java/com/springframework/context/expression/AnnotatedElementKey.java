package com.springframework.context.expression;

import com.springframework.util.Assert;
import com.sun.istack.internal.Nullable;

import java.lang.reflect.AnnotatedElement;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public final class AnnotatedElementKey implements Comparable<AnnotatedElementKey> {
    @Override
    public int compareTo(AnnotatedElementKey o) {
        return 0;
    }

    private final AnnotatedElement element;

    @Nullable
    private final Class<?> targetClass;


    /**
     * Create a new instance with the specified {@link AnnotatedElement} and
     * optional target {@link Class}.
     */
    public AnnotatedElementKey(AnnotatedElement element, @Nullable Class<?> targetClass) {
        Assert.notNull(element, "AnnotatedElement must not be null");
        this.element = element;
        this.targetClass = targetClass;
    }
}
