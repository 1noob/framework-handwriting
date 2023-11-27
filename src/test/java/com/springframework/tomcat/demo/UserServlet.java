package com.springframework.tomcat.demo;

import com.springframework.tomcat.Request;
import com.springframework.tomcat.Response;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class UserServlet extends MyHttpServlet {
    @Override
    public void doGet(Request request, Response response) {
        this.doPost(request, response);
    }

    @Override
    public void doPost(Request request, Response response) {
        try {
            //省略业务调用的代码，tomcat会根据request对象里面的inputStream拿到对应的参数进行业务调用
            //模拟业务层调用后的返回
            OutputStream outputStream = response.outputStream;
            String result = Response.responsebody + "user handle successful";
            outputStream.write(result.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() throws Exception {
        System.out.println("init=======");
    }

    public void destory() {
        System.out.println("destory========");
    }

}
