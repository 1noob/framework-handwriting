package com.springframework.web.context.support;

import com.springframework.beans.factory.BeanFactory;
import com.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.springframework.beans.factory.support.DefaultListableBeanFactory;
import com.springframework.context.ApplicationContext;
import com.springframework.core.env.Environment;
import com.springframework.core.io.Resource;

import java.io.IOException;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class XmlWebApplicationContext extends AbstractRefreshableWebApplicationContext {
    //Web应用中Spring配置文件的默认位置和名称，如果没有特别指定，则Spring会根据
    //此位置定义Spring Bean定义资源
    public static final String DEFAULT_CONFIG_LOCATION = "/WEB-INF/applicationContext.xml";
    //Spring Bean定义资源默认前缀
    public static final String DEFAULT_CONFIG_LOCATION_PREFIX = "/WEB-INF/";
    //Spring Bean定义资源默认后置
    public static final String DEFAULT_CONFIG_LOCATION_SUFFIX = ".xml";

    public XmlWebApplicationContext(ApplicationContext parent) {
        super(parent);
    }

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
    protected void closeBeanFactory() {

    }


    @Override
    public void setBeanName(String name) {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    //通过Spring容器刷新的refresh()方法触发
    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws Exception, IOException {
//        //为Spring容器创建XML Bean定义读取器，加载Spring Bean定义资源
//        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
//
//        // resource loading environment.
//        beanDefinitionReader.setEnvironment(getEnvironment());
//        //设置Bean定义读取器，因为XmlWebApplicationContext是DefaultResourceLoader的子类，所以使用默认资源加载器来定义Bean定义资源
//        beanDefinitionReader.setResourceLoader(this);
//        //为Bean定义读取器设置SAX实体解析器
//        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));
//
//        //在加载Bean定义之前，调用子类提供的一些用户自定义初始化Bean定义读取器的方法
//        initBeanDefinitionReader(beanDefinitionReader);
//        //使用Bean定义读取器加载Bean定义资源
//        loadBeanDefinitions(beanDefinitionReader);
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
}
