package com.springframework.context.annotation;

import com.springframework.beans.config.BeanDefinition;
import com.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.springframework.beans.factory.support.BeanNameGenerator;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class AnnotationBeanNameGenerator implements BeanNameGenerator {
    public static final AnnotationBeanNameGenerator INSTANCE = new AnnotationBeanNameGenerator();

    private static final String COMPONENT_ANNOTATION_CLASSNAME = "org.springframework.stereotype.Component";

    private final Map<String, Set<String>> metaAnnotationTypesCache = new ConcurrentHashMap<>();

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        return null;
    }
}
