package com.springframework.beans.factory.support;

import com.springframework.beans.config.BeanDefinition;
import com.springframework.core.io.AbstractResource;
import com.springframework.core.io.Resource;
import com.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
class BeanDefinitionResource extends AbstractResource {
    private final BeanDefinition beanDefinition;

    @Override
    public URL getURL() throws IOException {
        return null;
    }

    public BeanDefinitionResource(BeanDefinition beanDefinition) {
        Assert.notNull(beanDefinition, "BeanDefinition must not be null");
        this.beanDefinition = beanDefinition;
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getFilename() {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }
}
