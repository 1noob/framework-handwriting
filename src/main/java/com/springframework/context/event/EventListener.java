package com.springframework.context.event;

import com.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EventListener {

    /**
     * Alias for {@link #classes}.
     */
    @AliasFor("classes")
    Class<?>[] value() default {};

    /**
     * The event classes that this listener handles.
     * <p>If this attribute is specified with a single value, the
     * annotated method may optionally accept a single parameter.
     * However, if this attribute is specified with multiple values,
     * the annotated method must <em>not</em> declare any parameters.
     */
    @AliasFor("value")
    Class<?>[] classes() default {};


    String condition() default "";

}

