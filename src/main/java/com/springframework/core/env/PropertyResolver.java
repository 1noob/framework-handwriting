package com.springframework.core.env;

import com.sun.istack.internal.Nullable;

/**
 * @author Gary
 */
public interface PropertyResolver {
    String resolveRequiredPlaceholders(String text) throws IllegalArgumentException;
    @Nullable
    String getProperty(String key);
    String resolvePlaceholders(String text);

}
