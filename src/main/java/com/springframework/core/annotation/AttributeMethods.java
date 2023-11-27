package com.springframework.core.annotation;

import com.springframework.util.Assert;
import com.springframework.util.ConcurrentReferenceHashMap;
import com.springframework.util.ReflectionUtils;
import com.sun.istack.internal.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class AttributeMethods {
    private final Method[] attributeMethods;
    static final AttributeMethods NONE = new AttributeMethods(null, new Method[0]);
    private final Class<? extends Annotation> annotationType;

    private static final Map<Class<? extends Annotation>, AttributeMethods> cache =
            new ConcurrentReferenceHashMap<>();
    /**
     * Determine if this instance only contains a single attribute named
     * {@code value}.
     * @return {@code true} if there is only a value attribute
     */
    boolean hasOnlyValueAttribute() {
        return (this.attributeMethods.length == 1 &&
                MergedAnnotation.VALUE.equals(this.attributeMethods[0].getName()));
    }
    private void assertAnnotation(Annotation annotation) {
        Assert.notNull(annotation, "Annotation must not be null");
        if (this.annotationType != null) {
            Assert.isInstanceOf(this.annotationType, annotation);
        }
    }

    boolean isValid(Annotation annotation) {
        assertAnnotation(annotation);
        for (int i = 0; i < size(); i++) {
            if (canThrowTypeNotPresentException(i)) {
                try {
                    get(i).invoke(annotation);
                } catch (Throwable ex) {
                    return false;
                }
            }
        }
        return true;
    }

    boolean canThrowTypeNotPresentException(int index) {
        return this.canThrowTypeNotPresentException[index];
    }

    static AttributeMethods forAnnotationType(@Nullable Class<? extends Annotation> annotationType) {
        if (annotationType == null) {
            return NONE;
        }
        return cache.computeIfAbsent(annotationType, AttributeMethods::compute);
    }

    private static boolean isAttributeMethod(Method method) {
        return (method.getParameterCount() == 0 && method.getReturnType() != void.class);
    }

    private static AttributeMethods compute(Class<? extends Annotation> annotationType) {
        Method[] methods = annotationType.getDeclaredMethods();
        int size = methods.length;
        for (int i = 0; i < methods.length; i++) {
            if (!isAttributeMethod(methods[i])) {
                methods[i] = null;
                size--;
            }
        }
        if (size == 0) {
            return NONE;
        }
        Arrays.sort(methods, methodComparator);
        Method[] attributeMethods = Arrays.copyOf(methods, size);
        return new AttributeMethods(annotationType, attributeMethods);
    }

    private static final Comparator<Method> methodComparator = (m1, m2) -> {
        if (m1 != null && m2 != null) {
            return m1.getName().compareTo(m2.getName());
        }
        return m1 != null ? -1 : 1;
    };

    private final boolean[] canThrowTypeNotPresentException;

    private AttributeMethods(@Nullable Class<? extends Annotation> annotationType, Method[] attributeMethods) {
        this.annotationType = annotationType;
        this.attributeMethods = attributeMethods;
        this.canThrowTypeNotPresentException = new boolean[attributeMethods.length];
        boolean foundDefaultValueMethod = false;
        boolean foundNestedAnnotation = false;
        for (int i = 0; i < attributeMethods.length; i++) {
            Method method = this.attributeMethods[i];
            Class<?> type = method.getReturnType();
            if (method.getDefaultValue() != null) {
                foundDefaultValueMethod = true;
            }
            if (type.isAnnotation() || (type.isArray() && type.getComponentType().isAnnotation())) {
                foundNestedAnnotation = true;
            }
            ReflectionUtils.makeAccessible(method);
            this.canThrowTypeNotPresentException[i] = (type == Class.class || type == Class[].class || type.isEnum());
        }
        this.hasDefaultValueMethod = foundDefaultValueMethod;
        this.hasNestedAnnotation = foundNestedAnnotation;
    }

    private final boolean hasDefaultValueMethod;

    private final boolean hasNestedAnnotation;

    boolean hasNestedAnnotation() {
        return this.hasNestedAnnotation;
    }

    Method get(String name) {
        int index = indexOf(name);
        return index != -1 ? this.attributeMethods[index] : null;
    }

    int indexOf(String name) {
        for (int i = 0; i < this.attributeMethods.length; i++) {
            if (this.attributeMethods[i].getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public AttributeMethods(Method[] attributeMethods, Class<? extends Annotation> annotationType, boolean[] canThrowTypeNotPresentException, boolean hasDefaultValueMethod, boolean hasNestedAnnotation) {
        this.attributeMethods = attributeMethods;
        this.annotationType = annotationType;
        this.canThrowTypeNotPresentException = canThrowTypeNotPresentException;
        this.hasDefaultValueMethod = hasDefaultValueMethod;
        this.hasNestedAnnotation = hasNestedAnnotation;
    }

    Method get(int index) {
        return this.attributeMethods[index];
    }

    /**
     * Get the number of attributes in this collection.
     *
     * @return the number of attributes
     */
    int size() {
        return this.attributeMethods.length;
    }

    static String describe(@Nullable Method attribute) {
        if (attribute == null) {
            return "(none)";
        }
        return describe(attribute.getDeclaringClass(), attribute.getName());
    }

    static String describe(@Nullable Class<?> annotationType, @Nullable String attributeName) {
        if (attributeName == null) {
            return "(none)";
        }
        String in = (annotationType != null ? " in annotation [" + annotationType.getName() + "]" : "");
        return "attribute '" + attributeName + "'" + in;
    }

}
