package com.springframework.tomcat;

import java.io.OutputStream;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class Response {
    public OutputStream outputStream;

    public static final String responsebody = "HTTP/1.1 200+\r\n" + "Content-Type：text/html+\r\n"
            + "\r\n";

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
}
