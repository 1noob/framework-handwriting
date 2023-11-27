package com.springframework.core.annotation;

import com.springframework.util.Assert;
import com.sun.istack.internal.Nullable;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class MergedAnnotationPredicates {

    private MergedAnnotationPredicates() {
    }

    public static <A extends Annotation, K> Predicate<MergedAnnotation<A>> unique(
            Function<? super MergedAnnotation<A>, K> keyExtractor) {

        return new UniquePredicate<>(keyExtractor);
    }
    private static class UniquePredicate<A extends Annotation, K> implements Predicate<MergedAnnotation<A>> {

        private final Function<? super MergedAnnotation<A>, K> keyExtractor;

        private final Set<K> seen = new HashSet<>();

        UniquePredicate(Function<? super MergedAnnotation<A>, K> keyExtractor) {
            Assert.notNull(keyExtractor, "Key extractor must not be null");
            this.keyExtractor = keyExtractor;
        }

        @Override
        public boolean test(@Nullable MergedAnnotation<A> annotation) {
            K key = this.keyExtractor.apply(annotation);
            return this.seen.add(key);
        }
    }
}
