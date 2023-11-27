package com.springframework.tomcat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class TomcatServerV2 {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket=new ServerSocket(8080);
        System.out.println("====服务启动====");
        while(!serverSocket.isClosed()){
            Socket socket=serverSocket.accept();
            //对于每个连接，都开启一个线程
            RequestHandler requestHandler=new RequestHandler(socket);
            new Thread(requestHandler).start();

        }
    }
}
