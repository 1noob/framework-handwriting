package com.springframework.tomcat.demo;

import com.springframework.tomcat.Request;
import com.springframework.tomcat.Response;

public interface MyServlet {
    void init() throws Exception;

    void service(Request request, Response response) throws Exception;

    void destory();
}
