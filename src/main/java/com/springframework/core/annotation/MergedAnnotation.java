package com.springframework.core.annotation;

import com.springframework.context.annotation.AnnotationAttributes;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Predicate;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface MergedAnnotation<A extends Annotation> {
    String VALUE = "value";
    List<Class<? extends Annotation>> getMetaTypes();
    Optional<Object> getValue(String attributeName);
    <T> Optional<T> getValue(String attributeName, Class<T> type);
    int getDistance();
    Optional<A> synthesize(Predicate<? super MergedAnnotation<A>> condition) throws NoSuchElementException;
    boolean isPresent();
    MergedAnnotation<A> withNonMergedAttributes();
    boolean isDirectlyPresent();
    AnnotationAttributes asAnnotationAttributes(Adapt... adaptations);
    <T> Iterable<T> asMap(Adapt[] adaptations);

    enum Adapt {

        /**
         * Adapt class or class array attributes to strings.
         */
        CLASS_TO_STRING,

        /**
         * Adapt nested annotation or annotation arrays to maps rather
         * than synthesizing the values.
         */
        ANNOTATION_TO_MAP;

        protected final boolean isIn(Adapt... adaptations) {
            for (Adapt candidate : adaptations) {
                if (candidate == this) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Factory method to create an {@link Adapt} array from a set of boolean flags.
         * @param classToString if {@link Adapt#CLASS_TO_STRING} is included
         * @param annotationsToMap if {@link Adapt#ANNOTATION_TO_MAP} is included
         * @return a new {@link Adapt} array
         */
        public static Adapt[] values(boolean classToString, boolean annotationsToMap) {
            EnumSet<Adapt> result = EnumSet.noneOf(Adapt.class);
            addIfTrue(result, Adapt.CLASS_TO_STRING, classToString);
            addIfTrue(result, Adapt.ANNOTATION_TO_MAP, annotationsToMap);
            return result.toArray(new Adapt[0]);
        }

        private static <T> void addIfTrue(Set<T> result, T value, boolean test) {
            if (test) {
                result.add(value);
            }
        }
    }
}
