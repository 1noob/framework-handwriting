package com.springframework.context.support;

import com.springframework.beans.factory.BeanFactory;
import com.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.springframework.context.ApplicationContext;
import com.springframework.core.io.ClassPathResource;
import com.springframework.core.io.Resource;
import com.springframework.util.Assert;
import com.sun.istack.internal.Nullable;

import java.io.IOException;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class ClassPathXmlApplicationContext extends AbstractXmlApplicationContext {
    private Resource[] configResources;

    @Override
    public BeanFactory getParentBeanFactory() {
        return null;
    }

    @Override
    public boolean containsLocalBean(String name) {
        return false;
    }

    @Override
    public Object getBean(String name, Object... args) throws Exception {
        return null;
    }

    @Override
    public Object getBean(String name) throws Exception {
        return null;
    }

    public ClassPathXmlApplicationContext(String configLocation) throws Exception {
        this(new String[]{configLocation}, true, null);
    }

    public ClassPathXmlApplicationContext(String[] paths, Class<?> clazz, @Nullable ApplicationContext parent) throws Exception {
        super(parent);
        Assert.notNull(paths, "Path array must not be null");
        Assert.notNull(clazz, "Class argument must not be null");
        this.configResources = new Resource[paths.length];

        for (int i = 0; i < paths.length; ++i) {
            this.configResources[i] = new ClassPathResource(paths[i], clazz);
        }

        this.refresh();
    }

    public ClassPathXmlApplicationContext(
            String[] configLocations, boolean refresh, ApplicationContext parent)
            throws Exception {

        super(parent);
        setConfigLocations(configLocations);
        if (refresh) {
            refresh();
        }
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws RuntimeException {
        return null;
    }


    @Override
    public boolean containsBean(String name) {
        return false;
    }

    @Override
    public Class<?> getType(String name, boolean allowFactoryBeanInit) throws RuntimeException {
        return null;
    }

    @Override
    public Class<?> getType(String name) throws RuntimeException {
        return null;
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws Exception {
        return false;
    }

    @Override
    public Object getBean(Class<?> returnType, Object[] argsToUse) {
        return null;
    }

    @Override
    public void setBeanName(String name) {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }


    @Override
    protected void closeBeanFactory() {

    }


    @Override
    public String getApplicationName() {
        return null;
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return false;
    }

    @Override
    public void publishEvent(Object event) {

    }

    @Override
    protected Resource[] getConfigResources() {
        return this.configResources;
    }
}
