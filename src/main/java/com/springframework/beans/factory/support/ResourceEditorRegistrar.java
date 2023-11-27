package com.springframework.beans.factory.support;

import com.springframework.beans.PropertyEditorRegistrar;
import com.springframework.beans.PropertyEditorRegistry;
import com.springframework.core.env.PropertyResolver;
import com.springframework.core.io.ResourceLoader;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class ResourceEditorRegistrar implements PropertyEditorRegistrar {
    private final PropertyResolver propertyResolver;
    private final ResourceLoader resourceLoader;
    public ResourceEditorRegistrar(ResourceLoader resourceLoader, PropertyResolver propertyResolver) {
        this.resourceLoader = resourceLoader;
        this.propertyResolver = propertyResolver;
    }

    @Override
    public void registerCustomEditors(PropertyEditorRegistry registry) {

    }
}
