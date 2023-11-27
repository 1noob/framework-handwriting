package com.springframework.beans.factory.annotation;

import com.springframework.beans.config.BeanDefinition;
import com.springframework.beans.factory.support.GenericBeanDefinition;
import com.springframework.core.type.AnnotationMetadata;
import com.springframework.core.type.classreading.MetadataReader;
import com.springframework.util.Assert;

/**
 * @Author 虎哥
 * @Description @Component 以及派生注解定义 Bean
 * 多了一个 AnnotationMetadata 注解类元信息对象，例如通过 @Component 注解定义的 Bean 会解析成该对象
 * <p>
 * 要带着问题去学习,多猜想多验证
 **/
public class ScannedGenericBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition {
    private final AnnotationMetadata metadata;

    public ScannedGenericBeanDefinition(MetadataReader metadataReader) {
        Assert.notNull(metadataReader, "MetadataReader must not be null");
        this.metadata = metadataReader.getAnnotationMetadata();
        setBeanClassName(this.metadata.getClassName());
        setResource(metadataReader.getResource());
    }

    @Override
    public AnnotationMetadata getMetadata() {
        return null;
    }
}
