package com.springframework.core.annotation;

import com.sun.istack.internal.Nullable;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface MergedAnnotations extends Iterable<MergedAnnotation<Annotation>> {
    boolean isPresent(String annotationType);
    <A extends Annotation> Stream<MergedAnnotation<A>> stream(String annotationType);
    <A extends Annotation> MergedAnnotation<A> get(String annotationType,
                                                   @Nullable Predicate<? super MergedAnnotation<A>> predicate,
                                                   @Nullable MergedAnnotationSelector<A> selector);
    static MergedAnnotations from(AnnotatedElement element, SearchStrategy searchStrategy,
                                  RepeatableContainers repeatableContainers) {

        return TypeMappedAnnotations.from(element, searchStrategy, repeatableContainers, AnnotationFilter.PLAIN);
    }
    boolean isDirectlyPresent();
    static MergedAnnotations from(AnnotatedElement element) {
        return from(element, SearchStrategy.DIRECT);
    }
    static MergedAnnotations from(AnnotatedElement element, SearchStrategy searchStrategy) {
        return from(element, searchStrategy, RepeatableContainers.standardRepeatables());
    }
    <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType);

    <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType,
                                                   @Nullable Predicate<? super MergedAnnotation<A>> predicate,
                                                   @Nullable MergedAnnotationSelector<A> selector);
    enum SearchStrategy {

        /**
         * Find only directly declared annotations, without considering
         * {@link Inherited @Inherited} annotations and without searching
         * superclasses or implemented interfaces.
         */
        DIRECT,

        /**
         * Find all directly declared annotations as well as any
         * {@link Inherited @Inherited} superclass annotations. This strategy
         * is only really useful when used with {@link Class} types since the
         * {@link Inherited @Inherited} annotation is ignored for all other
         * {@linkplain AnnotatedElement annotated elements}. This strategy does
         * not search implemented interfaces.
         */
        INHERITED_ANNOTATIONS,

        /**
         * Find all directly declared and superclass annotations. This strategy
         * is similar to {@link #INHERITED_ANNOTATIONS} except the annotations
         * do not need to be meta-annotated with {@link Inherited @Inherited}.
         * This strategy does not search implemented interfaces.
         */
        SUPERCLASS,

        /**
         * Perform a full search of the entire type hierarchy, including
         * superclasses and implemented interfaces. Superclass annotations do
         * not need to be meta-annotated with {@link Inherited @Inherited}.
         */
        TYPE_HIERARCHY,

        /**
         * Perform a full search of the entire type hierarchy on the source
         * <em>and</em> any enclosing classes. This strategy is similar to
         * {@link #TYPE_HIERARCHY} except that {@linkplain Class#getEnclosingClass()
         * enclosing classes} are also searched. Superclass annotations do not
         * need to be meta-annotated with {@link Inherited @Inherited}. When
         * searching a {@link Method} source, this strategy is identical to
         * {@link #TYPE_HIERARCHY}.
         */
        TYPE_HIERARCHY_AND_ENCLOSING_CLASSES
    }

}
