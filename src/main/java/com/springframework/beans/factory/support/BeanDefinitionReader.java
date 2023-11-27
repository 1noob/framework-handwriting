package com.springframework.beans.factory.support;

import com.springframework.core.io.Resource;
import com.springframework.core.io.ResourceLoader;

/**
 * BeanDefinition 读取器，定义了加载资源的方法
 * @author Gary
 */
public interface BeanDefinitionReader {
    ResourceLoader getResourceLoader();
    int loadBeanDefinitions(Resource... resources) throws Exception;
    int loadBeanDefinitions(String location) throws Exception;
    int loadBeanDefinitions(Resource resource) throws Exception;
    ClassLoader getBeanClassLoader();
    BeanDefinitionRegistry getRegistry();
    int loadBeanDefinitions(String... locations) throws Exception;

}
