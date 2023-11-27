package com.springframework.core.env;

/**
 * @author Gary
 */
public interface ConfigurablePropertyResolver extends PropertyResolver {
    void validateRequiredProperties() throws Exception;

}
