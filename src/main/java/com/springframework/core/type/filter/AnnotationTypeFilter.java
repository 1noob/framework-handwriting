package com.springframework.core.type.filter;

import com.springframework.core.type.classreading.MetadataReaderFactory;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class AnnotationTypeFilter extends AbstractTypeHierarchyTraversingFilter {
    private final Class<? extends Annotation> annotationType;
    public final Class<? extends Annotation> getAnnotationType() {
        return this.annotationType;
    }
    private final boolean considerMetaAnnotations;

    public AnnotationTypeFilter(Class<? extends Annotation> annotationType, boolean considerMetaAnnotations) {
        this(annotationType, considerMetaAnnotations, false);
    }
    public AnnotationTypeFilter(Class<? extends Annotation> annotationType) {
        this(annotationType, true, false);
    }
    public AnnotationTypeFilter(
            Class<? extends Annotation> annotationType, boolean considerMetaAnnotations, boolean considerInterfaces) {

        super(annotationType.isAnnotationPresent(Inherited.class), considerInterfaces);
        this.annotationType = annotationType;
        this.considerMetaAnnotations = considerMetaAnnotations;
    }


    @Override
    public boolean match(com.springframework.core.type.classreading.MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        return false;
    }
}
