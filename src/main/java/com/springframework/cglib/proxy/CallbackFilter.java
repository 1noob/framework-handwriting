package com.springframework.cglib.proxy;

import java.lang.reflect.Method;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface CallbackFilter {
    int accept(Method var1);

    boolean equals(Object var1);
}
