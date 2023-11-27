package com.springframework.core.annotation;

import java.lang.annotation.Annotation;

@FunctionalInterface
public interface AnnotationFilter {
    AnnotationFilter PLAIN = packages("java.lang", "org.springframework.lang");

    /**
     * {@link AnnotationFilter} that always matches and can be used when no
     * relevant annotation types are expected to be present at all.
     */
    AnnotationFilter ALL = new AnnotationFilter() {
        @Override
        public boolean matches(Annotation annotation) {
            return true;
        }

        @Override
        public boolean matches(Class<?> type) {
            return true;
        }

        @Override
        public boolean matches(String typeName) {
            return true;
        }

        @Override
        public String toString() {
            return "All annotations filtered";
        }
    };


    @Deprecated
    AnnotationFilter NONE = new AnnotationFilter() {
        @Override
        public boolean matches(Annotation annotation) {
            return false;
        }

        @Override
        public boolean matches(Class<?> type) {
            return false;
        }

        @Override
        public boolean matches(String typeName) {
            return false;
        }

        @Override
        public String toString() {
            return "No annotation filtering";
        }
    };

    static AnnotationFilter packages(String... packages) {
        return new PackagesAnnotationFilter(packages);
    }
    /**
     * Test if the given annotation matches the filter.
     *
     * @param annotation the annotation to test
     * @return {@code true} if the annotation matches
     */
    default boolean matches(Annotation annotation) {
        return matches(annotation.annotationType());
    }

    /**
     * Test if the given type matches the filter.
     *
     * @param type the annotation type to test
     * @return {@code true} if the annotation matches
     */
    default boolean matches(Class<?> type) {
        return matches(type.getName());
    }

    /**
     * Test if the given type name matches the filter.
     *
     * @param typeName the fully qualified class name of the annotation type to test
     * @return {@code true} if the annotation matches
     */
    boolean matches(String typeName);


}

