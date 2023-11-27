package com.springframework.core.annotation;

import com.springframework.context.annotation.AnnotationAttributes;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
final class TypeMappedAnnotation<A extends Annotation> extends AbstractMergedAnnotation<A> {
    @Override
    protected <T> T getAttributeValue(String attributeName, Class<T> type) {
        return null;
    }

    @Override
    public List<Class<? extends Annotation>> getMetaTypes() {
        return null;
    }

    @Override
    public int getDistance() {
        return 0;
    }

    @Override
    public Optional<A> synthesize(Predicate<? super MergedAnnotation<A>> condition) throws NoSuchElementException {
        return Optional.empty();
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public MergedAnnotation<A> withNonMergedAttributes() {
        return null;
    }

    @Override
    public boolean isDirectlyPresent() {
        return false;
    }

    @Override
    public AnnotationAttributes asAnnotationAttributes(Adapt... adaptations) {
        return null;
    }

    @Override
    public <T> Iterable<T> asMap(Adapt[] adaptations) {
        return null;
    }
}
