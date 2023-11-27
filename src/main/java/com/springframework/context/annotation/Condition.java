package com.springframework.context.annotation;

import com.springframework.core.type.AnnotatedTypeMetadata;

@FunctionalInterface
public interface Condition {


    boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata);

}
