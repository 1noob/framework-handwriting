package com.springframework.core.env;

/**
 * @author Gary
 */
public interface Environment extends PropertyResolver {
    boolean acceptsProfiles(String... profiles);
    void merge(ConfigurableEnvironment parent);
}
