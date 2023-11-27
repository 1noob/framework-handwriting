package com.springframework.beans.factory.annotation;

import com.springframework.beans.config.BeanDefinition;
import com.springframework.core.type.AnnotationMetadata;

/**
 * @Author 虎哥
 * @Description 定义注解类的元信息，例如通过 @Component 注解定义的 Bean，那么注解类的元信息会包含编译后的 .class 文件的所有信息
 * 要带着问题去学习,多猜想多验证
 **/
public interface AnnotatedBeanDefinition extends BeanDefinition {
    AnnotationMetadata getMetadata();
}
