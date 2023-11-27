package com.springframework.web.context.request;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class SessionScope extends AbstractRequestAttributesScope {
    @Override
    protected int getScope() {
        return 0;
    }

    @Override
    public Object remove(String name) {
        return null;
    }
}
