package com.springframework.core.env;

import com.springframework.context.ApplicationContext;
import com.sun.istack.internal.Nullable;

import java.util.Map;

/**
 * @author Gary
 */
public interface ConfigurableEnvironment extends Environment, ConfigurablePropertyResolver {
    void setParent(@Nullable ApplicationContext parent);
    @Override
    void merge(ConfigurableEnvironment parent);
    Map<String, Object> getSystemProperties();
    Map<String, Object> getSystemEnvironment();
}
