package com.springframework.core.annotation;

import com.sun.istack.internal.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class AnnotatedElementUtils {
    public static <A extends Annotation> A findMergedAnnotation(AnnotatedElement element, Class<A> annotationType) {
        // Shortcut: directly present on the element, with no merging needed?
        if (AnnotationFilter.PLAIN.matches(annotationType) ||
                AnnotationsScanner.hasPlainJavaAnnotationsOnly(element)) {
            return element.getDeclaredAnnotation(annotationType);
        }
        // Exhaustive retrieval of merged annotations...
        return findAnnotations(element)
                .get(annotationType, null, MergedAnnotationSelectors.firstDirectlyDeclared())
                .synthesize(MergedAnnotation::isPresent).orElse(null);
    }

    private static MergedAnnotations findAnnotations(AnnotatedElement element) {
        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, RepeatableContainers.none());
    }

}
