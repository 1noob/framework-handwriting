package com.springframework.core.type;

import com.springframework.core.annotation.MergedAnnotations;
import com.springframework.core.annotation.RepeatableContainers;
import com.sun.istack.internal.Nullable;

import java.util.Set;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class StandardAnnotationMetadata extends StandardClassMetadata implements AnnotationMetadata {
    private final MergedAnnotations mergedAnnotations;

    private final boolean nestedAnnotationsAsMap;

    @Nullable
    private Set<String> annotationTypes;


    static AnnotationMetadata from(Class<?> introspectedClass) {
        return new StandardAnnotationMetadata(introspectedClass, true);
    }

    public StandardAnnotationMetadata(Class<?> introspectedClass, boolean nestedAnnotationsAsMap) {
        super(introspectedClass);
        this.mergedAnnotations = MergedAnnotations.from(introspectedClass,
                MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS, RepeatableContainers.none());
        this.nestedAnnotationsAsMap = nestedAnnotationsAsMap;
    }


    @Override
    public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
        return null;
    }

    @Override
    public MergedAnnotations getAnnotations() {
        return null;
    }

    @Override
    public boolean isInterface() {
        return false;
    }

    @Override
    public String getClassName() {
        return null;
    }

    @Override
    public boolean isIndependent() {
        return false;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }
}
