package com.springframework.web.context.request;

import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class RequestScope extends AbstractRequestAttributesScope {

    @Override
    protected int getScope() {
        return RequestAttributes.SCOPE_REQUEST;
    }

    @Override
    public Object remove(String name) {
        return null;
    }

    /**
     * There is no conversation id concept for a request, so this method
     * returns {@code null}.
     */


}

