package com.springframework.web.context.support;

import com.springframework.beans.factory.DisposableBean;
import com.springframework.beans.factory.ObjectFactory;
import com.springframework.beans.factory.config.Scope;
import com.springframework.util.Assert;

import javax.servlet.ServletContext;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class ServletContextScope implements Scope, DisposableBean {
    private final ServletContext servletContext;
    /**
     * Create a new Scope wrapper for the given ServletContext.
     * @param servletContext the ServletContext to wrap
     */
    public ServletContextScope(ServletContext servletContext) {
        Assert.notNull(servletContext, "ServletContext must not be null");
        this.servletContext = servletContext;
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        return null;
    }

    @Override
    public Object remove(String name) {
        return null;
    }
}
