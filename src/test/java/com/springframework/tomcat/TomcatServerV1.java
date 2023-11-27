package com.springframework.tomcat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author 虎哥
 * @Description Tomcat底层使用HTTP（TCP）
 * 使用BIO模型解决只能连接一次的弊端
 * 服务器一次只能连接一个客户端。tomcat在解决这个问题时使用了BIO模型，简单来讲就是每个连接一个线程，
 * <p>
 * 第一，提供 Socket 服务
 * Tomcat 的启动，必然是 Socket 服务，只不过它支持 HTTP 协议而已！
 * 这里其实可以扩展思考下，Tomcat 既然是基于 Socket，那么是基于BIO or NIO or AIO 呢？
 * 第二，进行请求的分发
 * 要知道一个 Tomcat 可以为多个 Web 应用提供服务，那么很显然，Tomcat 可以把 URL 下发到不同的Web应用。
 * 第三，需要把请求和响应封装成 request / response
 * 我们在 Web 应用这一层，可从来没有封装过 request/response 的，我们都是直接使用的，这就是因为 Tomcat 已经为你做好了！
 *
 * |要带着问题去学习,多猜想多验证|
 **/
public class TomcatServerV1 {
    public static void main(String[] args) throws IOException {
        //开启ServerSocket服务，设置端口号为8080
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("======服务启动成功========");
        //当服务没有关闭时
        while (!serverSocket.isClosed()) {
            //使用socket进行通信
            Socket socket = serverSocket.accept();
            //收到客户端发出的inputstream
            InputStream inputStream = socket.getInputStream();
            System.out.println("执行客户请求:" + Thread.currentThread());
            System.out.println("收到客户请求");
            //读取inputstream的内容
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            String msg = null;
            while ((msg = reader.readLine()) != null) {
                if (msg.length() == 0) break;
                System.out.println(msg);
            }
            //返回outputstream，主体内容是OK
            // String resp = "OK";
            String resp = Response.responsebody + "OK";

            OutputStream outputStream = socket.getOutputStream();
            System.out.println(resp);
            outputStream.write(resp.getBytes());
            outputStream.flush();
            outputStream.close();
            socket.close();
        }
    }
}
