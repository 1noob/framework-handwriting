package com.springframework.core.env;

import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class PropertySourcesPropertyResolver extends AbstractPropertyResolver {
    private final PropertySources propertySources;
    public PropertySourcesPropertyResolver(@Nullable PropertySources propertySources) {
        this.propertySources = propertySources;
    }

    @Override
    public void validateRequiredProperties() throws Exception {

    }


    @Override
    public String getProperty(String key) {
        return null;
    }

    @Override
    public String resolvePlaceholders(String text) {
        return null;
    }

    @Override
    protected String getPropertyAsRawString(String var1) {
        return null;
    }
}
