package com.mybatis.spring;

import com.springframework.beans.factory.FactoryBean;
import com.springframework.beans.factory.InitializingBean;
import com.springframework.context.ApplicationEvent;
import com.springframework.context.ApplicationListener;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class SqlSessionFactoryBean implements FactoryBean, InitializingBean, ApplicationListener {
    @Override
    public Object getObject() throws Exception {
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

    }
}
