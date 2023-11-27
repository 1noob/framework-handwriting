package com.springframework.aop.scope;

import com.springframework.aop.framework.AopInfrastructureBean;
import com.springframework.aop.framework.ProxyConfig;
import com.springframework.beans.factory.BeanFactory;
import com.springframework.beans.factory.BeanFactoryAware;
import com.springframework.beans.factory.FactoryBean;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class ScopedProxyFactoryBean extends ProxyConfig
        implements FactoryBean<Object>, BeanFactoryAware, AopInfrastructureBean {
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws RuntimeException {

    }

    @Override
    public Object getObject() throws Exception {
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }
}
