package com.springframework.context.support;

import com.springframework.beans.factory.support.DefaultListableBeanFactory;
import com.springframework.beans.factory.xml.ResourceEntityResolver;
import com.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import com.springframework.context.ApplicationContext;
import com.springframework.core.io.Resource;
import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class AbstractXmlApplicationContext extends AbstractRefreshableConfigApplicationContext {
    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws Exception {
        // Create a new XmlBeanDefinitionReader for the given BeanFactory.
        // 创建 XmlBeanDefinitionReader 对象
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

        // Configure the bean definition reader with this context's
        // resource loading environment.
        // 对 XmlBeanDefinitionReader 进行环境变量的设置
        beanDefinitionReader.setEnvironment(this.getEnvironment());
        beanDefinitionReader.setResourceLoader(this);
        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

        // Allow a subclass to provide custom initialization of the reader,
        // then proceed with actually loading the bean definitions.
        // 对 XmlBeanDefinitionReader 进行设置，可以进行覆盖
        initBeanDefinitionReader(beanDefinitionReader);

        // 从 Resource 们中，加载 BeanDefinition 们
        loadBeanDefinitions(beanDefinitionReader);
    }
    private boolean validating = true;
    protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader) {
        reader.setValidating(this.validating);
    }

    protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws Exception {
        // 从配置文件 Resource 中，加载 BeanDefinition 们
        Resource[] configResources = getConfigResources();
        if (configResources != null) {
            reader.loadBeanDefinitions(configResources);
        }
        // 从配置文件地址中，加载 BeanDefinition 们
        String[] configLocations = getConfigLocations();
        if (configLocations != null) {
            reader.loadBeanDefinitions(configLocations);
        }
    }


    @Nullable
    protected Resource[] getConfigResources() {
        return null;
    }
    public AbstractXmlApplicationContext(@Nullable ApplicationContext parent) {
        super(parent);
    }

}
