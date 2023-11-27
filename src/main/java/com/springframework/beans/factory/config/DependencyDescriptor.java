package com.springframework.beans.factory.config;

import com.springframework.beans.factory.InjectionPoint;
import com.springframework.core.MethodParameter;

import java.io.Serializable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class DependencyDescriptor extends InjectionPoint implements Serializable {
    public DependencyDescriptor(MethodParameter methodParameter, boolean b, boolean eager) {
        super();
    }
    public DependencyDescriptor(MethodParameter methodParameter, boolean required) {
        this(methodParameter, required, true);
    }
}

