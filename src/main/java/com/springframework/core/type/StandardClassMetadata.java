package com.springframework.core.type;

import com.springframework.util.Assert;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class StandardClassMetadata implements ClassMetadata {
    private final Class<?> introspectedClass;
    public StandardClassMetadata(Class<?> introspectedClass) {
        Assert.notNull(introspectedClass, "Class must not be null");
        this.introspectedClass = introspectedClass;
    }

    @Override
    public boolean isInterface() {
        return false;
    }

    @Override
    public String getClassName() {
        return null;
    }

    @Override
    public boolean isIndependent() {
        return false;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }
}
