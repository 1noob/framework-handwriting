package com.springframework.core;

import com.sun.istack.internal.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface ParameterNameDiscoverer {

    @Nullable
    String[] getParameterNames(Method method);

    @Nullable
    String[] getParameterNames(Constructor<?> ctor);
}
