package com.springframework.core;

/**
 * @author Gary
 */
public interface SmartClassLoader {
    boolean isClassReloadable(Class<?> clazz);

}
