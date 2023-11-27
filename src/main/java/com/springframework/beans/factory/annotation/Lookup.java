package com.springframework.beans.factory.annotation;

import java.lang.annotation.*;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lookup {

    /**
     * This annotation attribute may suggest a target bean name to look up.
     * If not specified, the target bean will be resolved based on the
     * annotated method's return type declaration.
     */
    String value() default "";

}
