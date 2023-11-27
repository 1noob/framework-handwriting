package com.springframework.cglib.core;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class DefaultGeneratorStrategy  implements GeneratorStrategy {
    public static final DefaultGeneratorStrategy INSTANCE = new DefaultGeneratorStrategy();

    public DefaultGeneratorStrategy() {
    }

    @Override
    public byte[] generate(ClassGenerator var1) throws Exception {
        return new byte[0];
    }
}
