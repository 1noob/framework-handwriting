package com.springframework.cglib.core;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/

public interface GeneratorStrategy {
    byte[] generate(ClassGenerator var1) throws Exception;

    boolean equals(Object var1);
}
