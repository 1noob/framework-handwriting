package com.springframework.web.context.request;

import com.springframework.util.Assert;
import com.sun.istack.internal.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class ServletRequestAttributes extends AbstractRequestAttributes {
    @Override
    public Object getAttribute(String name, int scope) {
        return null;
    }
    public final HttpServletResponse getResponse() {
        return this.response;
    }
    @Override
    public void setAttribute(String name, Object value, int scope) {

    }

    /**
     * Exposes the native {@link HttpServletRequest} that we're wrapping.
     */
    public final HttpServletRequest getRequest() {
        return this.request;
    }
    private final HttpServletRequest request;
    private HttpServletResponse response;

    public ServletRequestAttributes(HttpServletRequest request, @Nullable HttpServletResponse response) {
        this(request);
        this.response = response;
    }
    public ServletRequestAttributes(HttpServletRequest request) {
        Assert.notNull(request, "Request must not be null");
        this.request = request;
    }

}


