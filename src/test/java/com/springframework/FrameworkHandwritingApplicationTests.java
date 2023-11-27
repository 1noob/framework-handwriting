package com.springframework;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.junit.Test;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class FrameworkHandwritingApplicationTests {
    public static void main(String[] args) throws LifecycleException {
        //1.构建tomcat对象(遵循JAVAEE规范)
        Tomcat t = new Tomcat();
        //2.构建Connector对象(连接器),负责协议配置,端口配置等
        Connector conn = new Connector("HTTP/1.1");
        conn.setPort(8080);
        t.getService().addConnector(conn);
        //3.启动tomcat
        t.start();
        //4.阻塞当前线程
        t.getServer().await();
    }

    //嵌入式tomcat测试
    @Test
    public void testServlet() throws Exception {
        //构建tomcat对象,此对象为启动tomcat服务的入口对象
        Tomcat t = new Tomcat();
        //构建Connector对象,此对象负责与客户端的连接.
        Connector con = new Connector("HTTP/1.1");
        //设置服务端的监听端口
        con.setPort(8080);
        //将Connector注册到service中
        t.getService().addConnector(con);
        //注册servlet
        Context ctx = t.addContext("/", null);
        Tomcat.addServlet(
                ctx,
                "helloServlet",
                "com.springframework.servlet.HelloServlet");
        //映射servlet
        ctx.addServletMappingDecoded("/hello", "helloServlet");
        //启动tomcat
        t.start();
        //阻塞当前线程
        System.out.println(Thread.currentThread().getName());
        t.getServer().await();
        //while(true) {}
    }
}
