package com.springframework.core.annotation;

import com.springframework.util.ConcurrentReferenceHashMap;
import com.springframework.util.ReflectionUtils;
import com.sun.istack.internal.Nullable;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class RepeatableContainers {
    private final RepeatableContainers parent;

    public RepeatableContainers(RepeatableContainers parent) {
        this.parent = parent;
    }

    public static RepeatableContainers none() {
        return NoRepeatableContainers.INSTANCE;
    }

    /**
     * No repeatable containers.
     */
    private static class NoRepeatableContainers extends RepeatableContainers {

        private static NoRepeatableContainers INSTANCE = new NoRepeatableContainers();

        NoRepeatableContainers() {
            super(null);
        }
    }

    public static RepeatableContainers standardRepeatables() {
        return StandardRepeatableContainers.INSTANCE;
    }

    private static class StandardRepeatableContainers extends RepeatableContainers {

        private static final Map<Class<? extends Annotation>, Object> cache = new ConcurrentReferenceHashMap<>();

        private static final Object NONE = new Object();

        private static StandardRepeatableContainers INSTANCE = new StandardRepeatableContainers();

        StandardRepeatableContainers() {
            super(null);
        }

        @Override
        @Nullable
        Annotation[] findRepeatedAnnotations(Annotation annotation) {
            Method method = getRepeatedAnnotationsMethod(annotation.annotationType());
            if (method != null) {
                return (Annotation[]) ReflectionUtils.invokeMethod(method, annotation);
            }
            return super.findRepeatedAnnotations(annotation);
        }

        @Nullable
        private static Method getRepeatedAnnotationsMethod(Class<? extends Annotation> annotationType) {
            Object result = cache.computeIfAbsent(annotationType,
                    StandardRepeatableContainers::computeRepeatedAnnotationsMethod);
            return (result != NONE ? (Method) result : null);
        }

        private static Object computeRepeatedAnnotationsMethod(Class<? extends Annotation> annotationType) {
            AttributeMethods methods = AttributeMethods.forAnnotationType(annotationType);
            if (methods.hasOnlyValueAttribute()) {
                Method method = methods.get(0);
                Class<?> returnType = method.getReturnType();
                if (returnType.isArray()) {
                    Class<?> componentType = returnType.getComponentType();
                    if (Annotation.class.isAssignableFrom(componentType) &&
                            componentType.isAnnotationPresent(Repeatable.class)) {
                        return method;
                    }
                }
            }
            return NONE;
        }
    }

    Annotation[] findRepeatedAnnotations(Annotation annotation) {
        if (this.parent == null) {
            return null;
        }
        return this.parent.findRepeatedAnnotations(annotation);
    }
}
