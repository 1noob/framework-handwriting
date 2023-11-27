package com.springframework.core.annotation;

import com.springframework.util.ConcurrentReferenceHashMap;
import com.sun.istack.internal.Nullable;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class AnnotationTypeMappings {

    private static final Map<AnnotationFilter, Cache> standardRepeatablesCache = new ConcurrentReferenceHashMap<>();

    private static final Map<AnnotationFilter, Cache> noRepeatablesCache = new ConcurrentReferenceHashMap<>();

    private final RepeatableContainers repeatableContainers;


    private final AnnotationFilter filter;

    private final List<AnnotationTypeMapping> mappings;
    static void clearCache() {
        standardRepeatablesCache.clear();
        noRepeatablesCache.clear();
    }
    static AnnotationTypeMappings forAnnotationType(Class<? extends Annotation> annotationType) {
        return forAnnotationType(annotationType, AnnotationFilter.PLAIN);
    }
    AnnotationTypeMapping get(int index) {
        return this.mappings.get(index);
    }
    static AnnotationTypeMappings forAnnotationType(
            Class<? extends Annotation> annotationType, AnnotationFilter annotationFilter) {

        return forAnnotationType(annotationType,
                RepeatableContainers.standardRepeatables(), annotationFilter);
    }
    static AnnotationTypeMappings forAnnotationType(
            Class<? extends Annotation> annotationType,
            RepeatableContainers repeatableContainers,
            AnnotationFilter annotationFilter) {

        if (repeatableContainers == RepeatableContainers.standardRepeatables()) {
            return standardRepeatablesCache.computeIfAbsent(annotationFilter,
                    key -> new Cache(repeatableContainers, key)).get(annotationType);
        }
        if (repeatableContainers == RepeatableContainers.none()) {
            return noRepeatablesCache.computeIfAbsent(annotationFilter,
                    key -> new Cache(repeatableContainers, key)).get(annotationType);
        }
        return new AnnotationTypeMappings(repeatableContainers, annotationFilter,
                annotationType);
    }
    public static RepeatableContainers none() {
        return NoRepeatableContainers.INSTANCE;
    }
    /**
     * No repeatable containers.
     */
    private static class NoRepeatableContainers extends RepeatableContainers {

        private static NoRepeatableContainers INSTANCE = new NoRepeatableContainers();

        NoRepeatableContainers() {
            super(null);
        }
    }
    private AnnotationTypeMappings(RepeatableContainers repeatableContainers,
                                   AnnotationFilter filter, Class<? extends Annotation> annotationType) {

        this.repeatableContainers = repeatableContainers;
        this.filter = filter;
        this.mappings = new ArrayList<>();
        addAllMappings(annotationType);
        this.mappings.forEach(AnnotationTypeMapping::afterAllMappingsSet);
    }

    private void addAllMappings(Class<? extends Annotation> annotationType) {
        Deque<AnnotationTypeMapping> queue = new ArrayDeque<>();
        addIfPossible(queue, null, annotationType, null);
        while (!queue.isEmpty()) {
            AnnotationTypeMapping mapping = queue.removeFirst();
            this.mappings.add(mapping);
            addMetaAnnotationsToQueue(queue, mapping);
        }
    }
    private boolean isMappable(AnnotationTypeMapping source, @Nullable Annotation metaAnnotation) {
        return (metaAnnotation != null && !this.filter.matches(metaAnnotation) &&
                !AnnotationFilter.PLAIN.matches(source.getAnnotationType()) &&
                !isAlreadyMapped(source, metaAnnotation));
    }
    private boolean isAlreadyMapped(AnnotationTypeMapping source, Annotation metaAnnotation) {
        Class<? extends Annotation> annotationType = metaAnnotation.annotationType();
        AnnotationTypeMapping mapping = source;
        while (mapping != null) {
            if (mapping.getAnnotationType() == annotationType) {
                return true;
            }
            mapping = mapping.getSource();
        }
        return false;
    }
    private void addMetaAnnotationsToQueue(Deque<AnnotationTypeMapping> queue, AnnotationTypeMapping source) {
        Annotation[] metaAnnotations =
                AnnotationsScanner.getDeclaredAnnotations(source.getAnnotationType(), false);
        for (Annotation metaAnnotation : metaAnnotations) {
            if (!isMappable(source, metaAnnotation)) {
                continue;
            }
            Annotation[] repeatedAnnotations = this.repeatableContainers
                    .findRepeatedAnnotations(metaAnnotation);
            if (repeatedAnnotations != null) {
                for (Annotation repeatedAnnotation : repeatedAnnotations) {
                    if (!isMappable(source, metaAnnotation)) {
                        continue;
                    }
                    addIfPossible(queue, source, repeatedAnnotation);
                }
            }
            else {
                addIfPossible(queue, source, metaAnnotation);
            }
        }
    }
    private void addIfPossible(Deque<AnnotationTypeMapping> queue,
                               AnnotationTypeMapping source, Annotation ann) {

        addIfPossible(queue, source, ann.annotationType(), ann);
    }

    private static final IntrospectionFailureLogger failureLogger = IntrospectionFailureLogger.DEBUG;


    private void addIfPossible(Deque<AnnotationTypeMapping> queue, @Nullable AnnotationTypeMapping source,
                               Class<? extends Annotation> annotationType, @Nullable Annotation ann) {

        try {
            queue.addLast(new AnnotationTypeMapping(source, annotationType, ann));
        }
        catch (Exception ex) {
            AnnotationUtils.rethrowAnnotationConfigurationException(ex);
            if (failureLogger.isEnabled()) {
                failureLogger.log("Failed to introspect meta-annotation " + annotationType.getName(),
                        (source != null ? source.getAnnotationType() : null), ex);
            }
        }
    }
    /**
     * Cache created per {@link AnnotationFilter}.
     */
    private static class Cache {

        private final RepeatableContainers repeatableContainers;

        private final AnnotationFilter filter;

        private final Map<Class<? extends Annotation>, AnnotationTypeMappings> mappings;

        /**
         * Create a cache instance with the specified filter.
         * @param filter the annotation filter
         */
        Cache(RepeatableContainers repeatableContainers, AnnotationFilter filter) {
            this.repeatableContainers = repeatableContainers;
            this.filter = filter;
            this.mappings = new ConcurrentReferenceHashMap<>();
        }

        /**
         * Get or create {@link AnnotationTypeMappings} for the specified annotation type.
         * @param annotationType the annotation type
         * @return a new or existing {@link AnnotationTypeMappings} instance
         */
        AnnotationTypeMappings get(Class<? extends Annotation> annotationType) {
            return this.mappings.computeIfAbsent(annotationType, this::createMappings);
        }

        AnnotationTypeMappings createMappings(Class<? extends Annotation> annotationType) {
            return new AnnotationTypeMappings(this.repeatableContainers, this.filter, annotationType);
        }
    }
}
