package com.springframework.servlet;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class HelloServlet extends HttpServlet {

    @Override
    public void destroy() {
        System.out.println("destroy");
        super.destroy();
    }

    @Override
    public void init() throws ServletException {
        System.out.println("init");
        super.init();
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws IOException {
        PrintWriter printWriter = res.getWriter();
        printWriter.println("Hello World!");
//        super.service(req, res);
    }
}
