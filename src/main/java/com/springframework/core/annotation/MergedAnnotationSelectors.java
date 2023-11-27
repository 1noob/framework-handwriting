package com.springframework.core.annotation;

import java.lang.annotation.Annotation;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class MergedAnnotationSelectors {
    private static final MergedAnnotationSelector<?> FIRST_DIRECTLY_DECLARED = new FirstDirectlyDeclared();
    private static class FirstDirectlyDeclared implements MergedAnnotationSelector<Annotation> {

        @Override
        public boolean isBestCandidate(MergedAnnotation<Annotation> annotation) {
            return annotation.getDistance() == 0;
        }

        @Override
        public MergedAnnotation<Annotation> select(
                MergedAnnotation<Annotation> existing, MergedAnnotation<Annotation> candidate) {

            if (existing.getDistance() > 0 && candidate.getDistance() == 0) {
                return candidate;
            }
            return existing;
        }

    }



    public static <A extends Annotation> MergedAnnotationSelector<A> firstDirectlyDeclared() {
        return (MergedAnnotationSelector<A>) FIRST_DIRECTLY_DECLARED;
    }

}
