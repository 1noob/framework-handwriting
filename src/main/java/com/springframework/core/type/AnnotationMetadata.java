package com.springframework.core.type;

import java.util.Set;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface AnnotationMetadata extends ClassMetadata, AnnotatedTypeMetadata {
    static AnnotationMetadata introspect(Class<?> type) {
        return StandardAnnotationMetadata.from(type);
    }

    default boolean hasAnnotatedMethods(String annotationName) {
        return !getAnnotatedMethods(annotationName).isEmpty();
    }
    Set<MethodMetadata> getAnnotatedMethods(String annotationName);

}
