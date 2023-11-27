package com.springframework.core;

import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface ResolvableTypeProvider {

    /**
     * Return the {@link ResolvableType} describing this instance
     * (or {@code null} if some sort of default should be applied instead).
     */
    @Nullable
    ResolvableType getResolvableType();

}
