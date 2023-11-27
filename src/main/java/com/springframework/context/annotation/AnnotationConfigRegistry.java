package com.springframework.context.annotation;

/**
 * @author Gary
 */
public interface AnnotationConfigRegistry {

    void register(Class<?>... componentClasses);

    /**
     * Perform a scan within the specified base packages.
     * @param basePackages the packages to scan for component classes
     */
    void scan(String... basePackages);
}
