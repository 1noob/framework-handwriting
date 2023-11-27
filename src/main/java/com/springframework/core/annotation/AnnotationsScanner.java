package com.springframework.core.annotation;

import com.springframework.util.ConcurrentReferenceHashMap;
import com.sun.istack.internal.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
abstract class AnnotationsScanner {
    private static final Annotation[] NO_ANNOTATIONS = {};

    private static final Method[] NO_METHODS = {};

    static <A extends Annotation> A getDeclaredAnnotation(AnnotatedElement source, Class<A> annotationType) {
        Annotation[] annotations = getDeclaredAnnotations(source, false);
        for (Annotation annotation : annotations) {
            if (annotation != null && annotationType == annotation.annotationType()) {
                return (A) annotation;
            }
        }
        return null;
    }

    private static boolean isWithoutHierarchy(AnnotatedElement source, MergedAnnotations.SearchStrategy searchStrategy) {
        if (source == Object.class) {
            return true;
        }
        if (source instanceof Class) {
            Class<?> sourceClass = (Class<?>) source;
            boolean noSuperTypes = (sourceClass.getSuperclass() == Object.class &&
                    sourceClass.getInterfaces().length == 0);
            return (searchStrategy == MergedAnnotations.SearchStrategy.TYPE_HIERARCHY_AND_ENCLOSING_CLASSES ? noSuperTypes &&
                    sourceClass.getEnclosingClass() == null : noSuperTypes);
        }
        if (source instanceof Method) {
            Method sourceMethod = (Method) source;
            return (Modifier.isPrivate(sourceMethod.getModifiers()) ||
                    isWithoutHierarchy(sourceMethod.getDeclaringClass(), searchStrategy));
        }
        return true;
    }

    static boolean isKnownEmpty(AnnotatedElement source, MergedAnnotations.SearchStrategy searchStrategy) {
        if (hasPlainJavaAnnotationsOnly(source)) {
            return true;
        }
        if (searchStrategy == MergedAnnotations.SearchStrategy.DIRECT || isWithoutHierarchy(source, searchStrategy)) {
            if (source instanceof Method && ((Method) source).isBridge()) {
                return false;
            }
            return getDeclaredAnnotations(source, false).length == 0;
        }
        return false;
    }

    static boolean hasPlainJavaAnnotationsOnly(@Nullable Object annotatedElement) {
        if (annotatedElement instanceof Class) {
            return hasPlainJavaAnnotationsOnly((Class<?>) annotatedElement);
        } else if (annotatedElement instanceof Member) {
            return hasPlainJavaAnnotationsOnly(((Member) annotatedElement).getDeclaringClass());
        } else {
            return false;
        }
    }

    static Annotation[] getDeclaredAnnotations(AnnotatedElement source, boolean defensive) {
        boolean cached = false;
        Annotation[] annotations = declaredAnnotationCache.get(source);
        if (annotations != null) {
            cached = true;
        } else {
            annotations = source.getDeclaredAnnotations();
            if (annotations.length != 0) {
                boolean allIgnored = true;
                for (int i = 0; i < annotations.length; i++) {
                    Annotation annotation = annotations[i];
                    if (isIgnorable(annotation.annotationType()) ||
                            !AttributeMethods.forAnnotationType(annotation.annotationType()).isValid(annotation)) {
                        annotations[i] = null;
                    } else {
                        allIgnored = false;
                    }
                }
                annotations = (allIgnored ? NO_ANNOTATIONS : annotations);
                if (source instanceof Class || source instanceof Member) {
                    declaredAnnotationCache.put(source, annotations);
                    cached = true;
                }
            }
        }
        if (!defensive || annotations.length == 0 || !cached) {
            return annotations;
        }
        return annotations.clone();
    }

    private static boolean isIgnorable(Class<?> annotationType) {
        return AnnotationFilter.PLAIN.matches(annotationType);
    }

    private static final Map<AnnotatedElement, Annotation[]> declaredAnnotationCache =
            new ConcurrentReferenceHashMap<>(256);

    private static final Map<Class<?>, Method[]> baseTypeMethodsCache =
            new ConcurrentReferenceHashMap<>(256);


    static void clearCache() {
        declaredAnnotationCache.clear();
        baseTypeMethodsCache.clear();
    }
}
