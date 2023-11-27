package com.springframework.core.convert;

import com.springframework.core.ResolvableType;
import com.springframework.util.Assert;
import com.springframework.util.ObjectUtils;
import com.sun.istack.internal.Nullable;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class TypeDescriptor implements Serializable {
    private final Class<?> type;
    private final ResolvableType resolvableType;
    private final AnnotatedElementAdapter annotatedElement;
    private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];
    private static final Map<Class<?>, TypeDescriptor> commonTypesCache = new HashMap<>(32);
    public TypeDescriptor getMapKeyTypeDescriptor() {
        Assert.state(isMap(), "Not a [java.util.Map]");
        return getRelatedIfResolvable(this, getResolvableType().asMap().getGeneric(0));
    }
    public boolean isMap() {
        return Map.class.isAssignableFrom(getType());
    }
    public static TypeDescriptor valueOf(@Nullable Class<?> type) {
        if (type == null) {
            type = Object.class;
        }
        TypeDescriptor desc = commonTypesCache.get(type);
        return (desc != null ? desc : new TypeDescriptor(ResolvableType.forClass(type), null, null));
    }
    private class AnnotatedElementAdapter implements AnnotatedElement, Serializable {

        @Nullable
        private final Annotation[] annotations;

        public AnnotatedElementAdapter(@Nullable Annotation[] annotations) {
            this.annotations = annotations;
        }

        @Override
        public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
            for (Annotation annotation : getAnnotations()) {
                if (annotation.annotationType() == annotationClass) {
                    return true;
                }
            }
            return false;
        }

        @Override
        @Nullable
        @SuppressWarnings("unchecked")
        public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
            for (Annotation annotation : getAnnotations()) {
                if (annotation.annotationType() == annotationClass) {
                    return (T) annotation;
                }
            }
            return null;
        }

        @Override
        public Annotation[] getAnnotations() {
            return (this.annotations != null ? this.annotations.clone() : EMPTY_ANNOTATION_ARRAY);
        }

        @Override
        public Annotation[] getDeclaredAnnotations() {
            return getAnnotations();
        }

        public boolean isEmpty() {
            return ObjectUtils.isEmpty(this.annotations);
        }

        @Override
        public boolean equals(@Nullable Object other) {
            return (this == other || (other instanceof AnnotatedElementAdapter &&
                    Arrays.equals(this.annotations, ((AnnotatedElementAdapter) other).annotations)));
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(this.annotations);
        }

        @Override
        public String toString() {
            return TypeDescriptor.this.toString();
        }
    }
    public TypeDescriptor(Class<?> type, ResolvableType resolvableType, AnnotatedElementAdapter annotatedElement) {
        this.type = type;
        this.resolvableType = resolvableType;
        this.annotatedElement = annotatedElement;
    }
    public Annotation[] getAnnotations() {
        return this.annotatedElement.getAnnotations();
    }
    public TypeDescriptor(ResolvableType resolvableType, @Nullable Class<?> type, @Nullable Annotation[] annotations) {
        this.resolvableType = resolvableType;
        this.type = (type != null ? type : resolvableType.toClass());
        this.annotatedElement = new AnnotatedElementAdapter(annotations);
    }
    public TypeDescriptor getElementTypeDescriptor() {
        if (getResolvableType().isArray()) {
            return new TypeDescriptor(getResolvableType().getComponentType(), null, getAnnotations());
        }
        if (Stream.class.isAssignableFrom(getType())) {
            return getRelatedIfResolvable(this, getResolvableType().as(Stream.class).getGeneric(0));
        }
        return getRelatedIfResolvable(this, getResolvableType().asCollection().getGeneric(0));
    }
    public Class<?> getType() {
        return this.type;
    }
    public ResolvableType getResolvableType() {
        return this.resolvableType;
    }
    private static TypeDescriptor getRelatedIfResolvable(TypeDescriptor source, ResolvableType type) {
        if (type.resolve() == null) {
            return null;
        }
        return new TypeDescriptor(type, null, source.getAnnotations());
    }
}
