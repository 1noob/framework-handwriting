package com.springframework.beans.factory.config;

import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface BeanExpressionResolver {
    @Nullable
    Object evaluate(@Nullable String value, BeanExpressionContext evalContext) throws RuntimeException;

}
