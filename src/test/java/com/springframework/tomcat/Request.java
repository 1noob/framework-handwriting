package com.springframework.tomcat;

import lombok.Data;

import java.io.*;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
@Data
public class Request {
    //获取uri，如 /user
    private String uri;
    //获取请求方法，这里只写get和post GET or POST
    private String method;

    public Request(InputStream inputStream) {
        try {
            //获取inputStream
            BufferedReader read = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            //取HTTP请求响应的第一行，GET /user HTTP/1.1，按空格隔开
            String[] data = read.readLine().split(" ");
            //取uri和method
            this.uri = data[1];
            this.method = data[0];
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
