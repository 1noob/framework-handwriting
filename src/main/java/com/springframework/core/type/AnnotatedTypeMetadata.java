package com.springframework.core.type;

import com.springframework.core.annotation.*;
import com.springframework.util.MultiValueMap;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface AnnotatedTypeMetadata {
    default boolean isAnnotated(String annotationName) {
        return getAnnotations().isPresent(annotationName);
    }
    MergedAnnotations getAnnotations();
    default MultiValueMap<String, Object> getAllAnnotationAttributes(
            String annotationName, boolean classValuesAsString) {

        MergedAnnotation.Adapt[] adaptations = MergedAnnotation.Adapt.values(classValuesAsString, true);
        return getAnnotations().stream(annotationName)
                .filter(MergedAnnotationPredicates.unique(MergedAnnotation::getMetaTypes))
                .map(MergedAnnotation::withNonMergedAttributes)
                .collect(MergedAnnotationCollectors.toMultiValueMap(map ->
                        map.isEmpty() ? null : map, adaptations));
    }

    default Map<String, Object> getAnnotationAttributes(String annotationName,
                                                        boolean classValuesAsString) {

        MergedAnnotation<Annotation> annotation = getAnnotations().get(annotationName,
                null, MergedAnnotationSelectors.firstDirectlyDeclared());
        if (!annotation.isPresent()) {
            return null;
        }
        return annotation.asAnnotationAttributes(MergedAnnotation.Adapt.values(classValuesAsString, true));
    }
}
