package com.springframework.tomcat;

import java.io.*;
import java.net.Socket;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class RequestHandler implements Runnable {
    public Socket socket;

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    //继承Runnable接口，实现run方法
    public void run() {
        InputStream inputStream = null;
        try {
            inputStream = socket.getInputStream();
            System.out.println("执行客户请求" + Thread.currentThread());
            System.out.println("====收到客户端请求====");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            String msg = null;
            while ((msg = reader.readLine()) != null) {
                if (msg.length() == 0) {
                    break;
                }
                System.out.println(msg);
            }
            String resp = Response.responsebody + "Tomcat ------ OK";
            OutputStream outputStream = socket.getOutputStream();
            System.out.println(resp);
            outputStream.write(resp.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
