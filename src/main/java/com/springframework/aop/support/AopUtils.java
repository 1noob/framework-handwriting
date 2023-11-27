package com.springframework.aop.support;

import com.springframework.core.BridgeMethodResolver;
import com.springframework.util.ClassUtils;
import com.sun.istack.internal.Nullable;

import java.lang.reflect.Method;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class AopUtils {
    public static Method getMostSpecificMethod(Method method, @Nullable Class<?> targetClass) {
        Class<?> specificTargetClass = (targetClass != null ? ClassUtils.getUserClass(targetClass) : null);
        Method resolvedMethod = ClassUtils.getMostSpecificMethod(method, specificTargetClass);
        // If we are dealing with method with generic parameters, find the original method.
        return BridgeMethodResolver.findBridgedMethod(resolvedMethod);
    }
}
