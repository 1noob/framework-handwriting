package com.springframework.cglib.proxy;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface NoOp extends Callback {
    NoOp INSTANCE = new NoOp() {
    };
}

