package com.springframework.core.env;

import com.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class StandardEnvironment extends AbstractEnvironment {
    @Override
    public boolean acceptsProfiles(String... profiles) {
        return false;
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
    public void setParent(ApplicationContext parent) {

    }

    @Override
    public void merge(ConfigurableEnvironment parent) {

    }

    @Override
    public Map<String, Object> getSystemProperties() {
        return null;
    }

    @Override
    public Map<String, Object> getSystemEnvironment() {
        return null;
    }

    @Override
    public void validateRequiredProperties() throws Exception {

    }
}
