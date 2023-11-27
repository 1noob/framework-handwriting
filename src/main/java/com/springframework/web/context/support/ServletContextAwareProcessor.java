package com.springframework.web.context.support;

import com.springframework.beans.factory.config.BeanPostProcessor;
import com.sun.istack.internal.Nullable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class ServletContextAwareProcessor implements BeanPostProcessor {
    @Nullable
    private ServletContext servletContext;

    @Nullable
    private ServletConfig servletConfig;

    /**
     * Create a new ServletContextAwareProcessor for the given context and config.
     */
    public ServletContextAwareProcessor(@Nullable ServletContext servletContext, @Nullable ServletConfig servletConfig) {
        this.servletContext = servletContext;
        this.servletConfig = servletConfig;
    }
}
