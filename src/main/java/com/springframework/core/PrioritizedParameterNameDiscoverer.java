package com.springframework.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class PrioritizedParameterNameDiscoverer implements ParameterNameDiscoverer {
    @Override
    public String[] getParameterNames(Method method) {
        return new String[0];
    }

    @Override
    public String[] getParameterNames(Constructor<?> ctor) {
        return new String[0];
    }
}
