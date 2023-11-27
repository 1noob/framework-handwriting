package com.springframework.core;

import com.springframework.util.Assert;
import com.springframework.util.ConcurrentReferenceHashMap;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public final class GenericTypeResolver {
    private static final Map<Class<?>, Map<TypeVariable, Type>> typeVariableCache = new ConcurrentReferenceHashMap<>();

    public static Class<?> resolveReturnType(Method method, Class<?> clazz) {
        Assert.notNull(method, "Method must not be null");
        Assert.notNull(clazz, "Class must not be null");
        return ResolvableType.forMethodReturnType(method, clazz).resolve(method.getReturnType());
    }
    private GenericTypeResolver() {
    }
}
