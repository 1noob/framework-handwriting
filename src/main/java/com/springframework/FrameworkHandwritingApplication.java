package com.springframework;

import com.springframework.context.support.ClassPathXmlApplicationContext;
import com.springframework.debug.domain.User;

/**
 * @author Gary
 */
public class FrameworkHandwritingApplication {

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("dependency-lookup-context.xml");
        User user = context.getBean("user", User.class);
        System.out.println(user);
    }

}
