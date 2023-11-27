package com.springframework.cglib.core;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class Signature {
    private String name;
    private String desc;

    public Signature(String name, String desc) {
        if (name.indexOf(40) >= 0) {
            throw new IllegalArgumentException("Name '" + name + "' is invalid");
        } else {
            this.name = name;
            this.desc = desc;
        }
    }
}
