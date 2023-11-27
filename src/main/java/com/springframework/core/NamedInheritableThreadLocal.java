package com.springframework.core;

import com.springframework.util.Assert;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class NamedInheritableThreadLocal<T> extends InheritableThreadLocal<T> {

    private final String name;


    /**
     * Create a new NamedInheritableThreadLocal with the given name.
     *
     * @param name a descriptive name for this ThreadLocal
     */
    public NamedInheritableThreadLocal(String name) {
        Assert.hasText(name, "Name must not be empty");
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
