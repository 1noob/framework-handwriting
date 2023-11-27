package com.springframework.beans.factory.annotation;

import com.springframework.beans.config.BeanDefinition;
import com.springframework.beans.factory.support.GenericBeanDefinition;
import com.springframework.core.type.AnnotationMetadata;

/**
 * @Author 虎哥
 * @Description 借助于 @Import 导入 Bean
 * 通过 @Import 导入的 Configuration Class 会解析成该对象,不过 factoryMethodMetadata 还是为 null
 *
 * 要带着问题去学习,多猜想多验证
 **/
public class AnnotatedGenericBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition {
    protected AnnotatedGenericBeanDefinition(BeanDefinition original, AnnotationMetadata metadata) {
        super(original);
        this.metadata = metadata;
    }
    private final AnnotationMetadata metadata;

    public AnnotatedGenericBeanDefinition(Class<?> beanClass) {
        setBeanClass(beanClass);
        this.metadata = AnnotationMetadata.introspect(beanClass);
    }

    @Override
    public AnnotationMetadata getMetadata() {
        return null;
    }
}
