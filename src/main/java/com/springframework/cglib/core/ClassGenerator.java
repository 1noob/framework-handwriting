package com.springframework.cglib.core;

import com.springframework.asm.ClassVisitor;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface ClassGenerator {
    void generateClass(ClassVisitor var1) throws Exception;
}
