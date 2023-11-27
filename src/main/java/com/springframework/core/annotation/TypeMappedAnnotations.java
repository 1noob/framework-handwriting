package com.springframework.core.annotation;

import com.sun.istack.internal.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.springframework.core.annotation.AnnotationsScanner.*;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
final class TypeMappedAnnotations implements MergedAnnotations {
    @Override
    public Iterator<MergedAnnotation<Annotation>> iterator() {
        return null;
    }

    static final MergedAnnotations NONE = new TypeMappedAnnotations(
            null, new Annotation[0], RepeatableContainers.none(), AnnotationFilter.ALL);


    static MergedAnnotations from(AnnotatedElement element, SearchStrategy searchStrategy,
                                  RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter) {

        if (AnnotationsScanner.isKnownEmpty(element, searchStrategy)) {
            return NONE;
        }
        return new TypeMappedAnnotations(element, searchStrategy, repeatableContainers, annotationFilter);
    }

    @Nullable
    private final Object source;

    @Nullable
    private final AnnotatedElement element;

    @Nullable
    private final SearchStrategy searchStrategy;

    @Nullable
    private final Annotation[] annotations;

    private final RepeatableContainers repeatableContainers;

    private final AnnotationFilter annotationFilter;


    private TypeMappedAnnotations(AnnotatedElement element, SearchStrategy searchStrategy,
                                  RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter) {

        this.source = element;
        this.element = element;
        this.searchStrategy = searchStrategy;
        this.annotations = null;
        this.repeatableContainers = repeatableContainers;
        this.annotationFilter = annotationFilter;
    }

    private TypeMappedAnnotations(@Nullable Object source, Annotation[] annotations,
                                  RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter) {

        this.source = source;
        this.element = null;
        this.searchStrategy = null;
        this.annotations = annotations;
        this.repeatableContainers = repeatableContainers;
        this.annotationFilter = annotationFilter;
    }

    @Override
    public boolean isPresent(String annotationType) {
        return false;
    }

    @Override
    public <A extends Annotation> Stream<MergedAnnotation<A>> stream(String annotationType) {
        return null;
    }

    @Override
    public <A extends Annotation> MergedAnnotation<A> get(String annotationType, Predicate<? super MergedAnnotation<A>> predicate, MergedAnnotationSelector<A> selector) {
        return null;
    }

    @Override
    public boolean isDirectlyPresent() {
        return false;
    }

    @Override
    public <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType) {
        return null;
    }

    @Override
    public <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType, Predicate<? super MergedAnnotation<A>> predicate, MergedAnnotationSelector<A> selector) {
        return null;
    }
}
