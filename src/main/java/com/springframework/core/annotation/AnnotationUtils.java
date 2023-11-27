package com.springframework.core.annotation;

import java.lang.annotation.Annotation;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class AnnotationUtils {
    /**
     * Clear the internal annotation metadata cache.
     * @since 4.3.15
     */
    public static void clearCache() {
        AnnotationTypeMappings.clearCache();
        AnnotationsScanner.clearCache();
    }
    static void rethrowAnnotationConfigurationException(Throwable ex) {
        if (ex instanceof AnnotationConfigurationException) {
            throw (AnnotationConfigurationException) ex;
        }
    }
    public static boolean isAnnotationDeclaredLocally(Class<? extends Annotation> annotationType, Class<?> clazz) {
        return MergedAnnotations.from(clazz).get(annotationType).isDirectlyPresent();
    }
}
