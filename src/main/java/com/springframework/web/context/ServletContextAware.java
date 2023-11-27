package com.springframework.web.context;

import com.springframework.beans.factory.Aware;

import javax.servlet.ServletContext;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface ServletContextAware extends Aware {
    void setServletContext(ServletContext servletContext);
}
