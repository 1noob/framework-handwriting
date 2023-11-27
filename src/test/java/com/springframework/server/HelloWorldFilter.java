package com.springframework.server;

import javax.servlet.*;
import java.io.IOException;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class HelloWorldFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("触发 Hello World 过滤器...");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}

