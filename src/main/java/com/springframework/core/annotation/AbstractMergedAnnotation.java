package com.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
abstract class AbstractMergedAnnotation<A extends Annotation> implements MergedAnnotation<A> {
    @Override
    public Optional<Object> getValue(String attributeName) {
        return getValue(attributeName, Object.class);
    }

    @Override
    public <T> Optional<T> getValue(String attributeName, Class<T> type) {
        return Optional.ofNullable(getAttributeValue(attributeName, type));
    }

    protected abstract <T> T getAttributeValue(String attributeName, Class<T> type);

}
