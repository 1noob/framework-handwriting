package com.springframework.cglib.core;


/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface NamingPolicy {
    String getClassName(String var1, String var2, Object var3, Predicate var4);

    @Override
    boolean equals(Object var1);
}

