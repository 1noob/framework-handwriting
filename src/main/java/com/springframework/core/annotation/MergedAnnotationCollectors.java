package com.springframework.core.annotation;

import com.springframework.util.LinkedMultiValueMap;
import com.springframework.util.MultiValueMap;

import java.lang.annotation.Annotation;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class MergedAnnotationCollectors {
    private static final Collector.Characteristics[] NO_CHARACTERISTICS = {};

    private static final Collector.Characteristics[] IDENTITY_FINISH_CHARACTERISTICS = {Collector.Characteristics.IDENTITY_FINISH};

    private static <K, V> MultiValueMap<K, V> merge(MultiValueMap<K, V> map,
                                                    MultiValueMap<K, V> additions) {
        map.addAll(additions);
        return map;
    }
    public static <A extends Annotation> Collector<MergedAnnotation<A>, ?, MultiValueMap<String, Object>> toMultiValueMap(
            Function<MultiValueMap<String, Object>, MultiValueMap<String, Object>> finisher,
            MergedAnnotation.Adapt... adaptations) {

        Collector.Characteristics[] characteristics = (isSameInstance(finisher, Function.identity()) ?
                IDENTITY_FINISH_CHARACTERISTICS : NO_CHARACTERISTICS);
        return Collector.of(LinkedMultiValueMap::new,
                (map, annotation) -> annotation.asMap(adaptations).forEach(map::add),
                MergedAnnotationCollectors::merge, finisher, characteristics);
    }
    private static boolean isSameInstance(Object instance, Object candidate) {
        return instance == candidate;
    }
}
