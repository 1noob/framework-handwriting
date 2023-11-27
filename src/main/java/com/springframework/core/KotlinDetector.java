package com.springframework.core;

import com.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;

import static com.springframework.util.ClassUtils.forName;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class KotlinDetector {

    private static final Class<? extends Annotation> kotlinMetadata;

    private static final boolean kotlinReflectPresent;

    static {
        Class<?> metadata;
        ClassLoader classLoader = KotlinDetector.class.getClassLoader();
        try {
            metadata = forName("kotlin.Metadata", classLoader);
        } catch (ClassNotFoundException ex) {
            // Kotlin API not available - no Kotlin support
            metadata = null;
        }
        kotlinMetadata = (Class<? extends Annotation>) metadata;
        kotlinReflectPresent = ClassUtils.isPresent("kotlin.reflect.full.KClasses", classLoader);
    }

    public static boolean isPresent(String className, ClassLoader classLoader) {
        try {
            forName(className, classLoader);
            return true;
        }
        catch (IllegalAccessError err) {
            throw new IllegalStateException("Readability mismatch in inheritance hierarchy of class [" +
                    className + "]: " + err.getMessage(), err);
        }
        catch (Throwable ex) {
            // Typically ClassNotFoundException or NoClassDefFoundError...
            return false;
        }
    }
    /**
     * Determine whether Kotlin is present in general.
     */
    public static boolean isKotlinPresent() {
        return (kotlinMetadata != null);
    }

    /**
     * Determine whether Kotlin reflection is present.
     *
     * @since 5.1
     */
    public static boolean isKotlinReflectPresent() {
        return kotlinReflectPresent;
    }

    /**
     * Determine whether the given {@code Class} is a Kotlin type
     * (with Kotlin metadata present on it).
     */
    public static boolean isKotlinType(Class<?> clazz) {
        return (kotlinMetadata != null && clazz.getDeclaredAnnotation(kotlinMetadata) != null);
    }

}
