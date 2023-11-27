package com.springframework.servlet3;

import com.springframework.server.HelloWorldFilter;
import com.springframework.server.HelloWorldServlet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.EnumSet;
import java.util.Set;

/**
 * @Author 虎哥
 * @Description 在实现的 onStartup 方法中向
 * ServletContext 对象（Servlet 上下文）添加之前在 web.xml 中配置的
 * HelloWorldFilter 和 HelloWorldServlet，这样一来就可以去除 web.xml 文件了。
 * |要带着问题去学习,多猜想多验证|
 **/
public class CustomServletContainerInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        System.out.println("创建 Hello World Servlet...");
        javax.servlet.ServletRegistration.Dynamic servlet = ctx.addServlet(
                HelloWorldServlet.class.getSimpleName(), HelloWorldServlet.class);
        servlet.addMapping("/hello");

        System.out.println("创建 Hello World Filter...");
        javax.servlet.FilterRegistration.Dynamic filter =
                ctx.addFilter(HelloWorldFilter.class.getSimpleName(), HelloWorldFilter.class);
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.allOf(DispatcherType.class);
        dispatcherTypes.add(DispatcherType.REQUEST);
        dispatcherTypes.add(DispatcherType.FORWARD);
        filter.addMappingForUrlPatterns(dispatcherTypes, true, "/hello");
    }
}
