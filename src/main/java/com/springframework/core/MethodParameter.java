package com.springframework.core;

import com.springframework.util.Assert;
import com.springframework.util.ClassUtils;
import com.sun.istack.internal.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class MethodParameter {


    public MethodParameter withContainingClass(@Nullable Class<?> containingClass) {
        MethodParameter result = clone();
        result.containingClass = containingClass;
        result.parameterType = null;
        return result;
    }
    @Override
    public MethodParameter clone() {
        return new MethodParameter(this);
    }

    public Class<?> getParameterType() {
        Class<?> paramType = this.parameterType;
        if (paramType != null) {
            return paramType;
        }
        if (getContainingClass() != getDeclaringClass()) {
            paramType = ResolvableType.forMethodParameter(this, null, 1).resolve();
        }
        if (paramType == null) {
            paramType = computeParameterType();
        }
        this.parameterType = paramType;
        return paramType;
    }
    private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];


    private final Executable executable;

    private final int parameterIndex;

    @Nullable
    private volatile Parameter parameter;

    private int nestingLevel;

    /**
     * Map from Integer level to Integer type index.
     */
    @Nullable
    Map<Integer, Integer> typeIndexesPerLevel;

    /**
     * The containing class. Could also be supplied by overriding {@link #getContainingClass()}
     */
    @Nullable
    private volatile Class<?> containingClass;

    @Nullable
    private volatile Class<?> parameterType;

    @Nullable
    private volatile Type genericParameterType;

    @Nullable
    private volatile Annotation[] parameterAnnotations;


    @Nullable
    private volatile String parameterName;

    @Nullable
    private volatile MethodParameter nestedMethodParameter;

    public Type getGenericParameterType() {
        Type paramType = this.genericParameterType;
        if (paramType == null) {
            if (this.parameterIndex < 0) {
                Method method = getMethod();
//                paramType = (method != null ?
//                        (KotlinDetector.isKotlinReflectPresent() && KotlinDetector.isKotlinType(getContainingClass()) ?
//                                KotlinDelegate.getGenericReturnType(method) : method.getGenericReturnType()) : void.class);
            }
            else {
                Type[] genericParameterTypes = this.executable.getGenericParameterTypes();
                int index = this.parameterIndex;
                if (this.executable instanceof Constructor &&
                        ClassUtils.isInnerClass(this.executable.getDeclaringClass()) &&
                        genericParameterTypes.length == this.executable.getParameterCount() - 1) {
                    // Bug in javac: type array excludes enclosing instance parameter
                    // for inner classes with at least one generic constructor parameter,
                    // so access it with the actual parameter index lowered by 1
                    index = this.parameterIndex - 1;
                }
                paramType = (index >= 0 && index < genericParameterTypes.length ?
                        genericParameterTypes[index] : computeParameterType());
            }
            this.genericParameterType = paramType;
        }
        return paramType;
    }
    private Class<?> computeParameterType() {
        if (this.parameterIndex < 0) {
            Method method = getMethod();
            if (method == null) {
                return void.class;
            }
//            if (KotlinDetector.isKotlinReflectPresent() && KotlinDetector.isKotlinType(getContainingClass())) {
//                return KotlinDelegate.getReturnType(method);
//            }
            return method.getReturnType();
        }
        return this.executable.getParameterTypes()[this.parameterIndex];
    }
    /**
     * Create a new {@code MethodParameter} for the given method, with nesting level 1.
     * @param method         the Method to specify a parameter for
     * @param parameterIndex the index of the parameter: -1 for the method
     *                       return type; 0 for the first method parameter; 1 for the second method
     * @param executable
     * @param executable1
     */
    public MethodParameter(Method method, int parameterIndex, Executable executable, Executable executable1) {
        this(method, parameterIndex, 1);
    }
    public MethodParameter(Method method, int parameterIndex) {
        this(method, parameterIndex, 1);
    }
    @Nullable
    private volatile ParameterNameDiscoverer parameterNameDiscoverer;
    public MethodParameter(MethodParameter original) {
        Assert.notNull(original, "Original must not be null");
        this.executable = original.executable;
        this.parameterIndex = original.parameterIndex;
        this.parameter = original.parameter;
        this.nestingLevel = original.nestingLevel;
        this.typeIndexesPerLevel = original.typeIndexesPerLevel;
        this.containingClass = original.containingClass;
        this.parameterType = original.parameterType;
        this.genericParameterType = original.genericParameterType;
        this.parameterAnnotations = original.parameterAnnotations;
        this.parameterNameDiscoverer = original.parameterNameDiscoverer;
        this.parameterName = original.parameterName;
    }

    /**
     * Create a new {@code MethodParameter} for the given method.
     *
     * @param method         the Method to specify a parameter for
     * @param parameterIndex the index of the parameter: -1 for the method
     *                       return type; 0 for the first method parameter; 1 for the second method
     *                       parameter, etc.
     * @param nestingLevel   the nesting level of the target type
     *                       (typically 1; e.g. in case of a List of Lists, 1 would indicate the
     *                       nested List, whereas 2 would indicate the element of the nested List)
     */
    public MethodParameter(Method method, int parameterIndex, int nestingLevel) {
        Assert.notNull(method, "Method must not be null");
        this.executable = method;
        this.parameterIndex = validateIndex(method, parameterIndex);
        this.nestingLevel = nestingLevel;
    }

    private static int validateIndex(Executable executable, int parameterIndex) {
        int count = executable.getParameterCount();
        Assert.isTrue(parameterIndex >= -1 && parameterIndex < count,
                () -> "Parameter index needs to be between -1 and " + (count - 1));
        return parameterIndex;
    }

    /**
     * Create a new MethodParameter for the given constructor, with nesting level 1.
     * @param constructor    the Constructor to specify a parameter for
     * @param parameterIndex the index of the parameter
     * @param executable
     * @param executable1
     */
    public MethodParameter(Constructor<?> constructor, int parameterIndex, Executable executable, Executable executable1) {
        this(constructor, parameterIndex, 1);
    }

    /**
     * Create a new MethodParameter for the given constructor.
     *
     * @param constructor    the Constructor to specify a parameter for
     * @param parameterIndex the index of the parameter
     * @param nestingLevel   the nesting level of the target type
     *                       (typically 1; e.g. in case of a List of Lists, 1 would indicate the
     *                       nested List, whereas 2 would indicate the element of the nested List)
     */
    public MethodParameter(Constructor<?> constructor, int parameterIndex, int nestingLevel) {
        Assert.notNull(constructor, "Constructor must not be null");
        this.executable = constructor;
        this.parameterIndex = validateIndex(constructor, parameterIndex);
        this.nestingLevel = nestingLevel;
    }

    /**
     * Internal constructor used to create a {@link MethodParameter} with a
     * containing class already set.
     *
     * @param executable      the Executable to specify a parameter for
     * @param parameterIndex  the index of the parameter
     * @param containingClass the containing class
     * @since 5.2
     */
    MethodParameter(Executable executable, int parameterIndex, @Nullable Class<?> containingClass) {
        Assert.notNull(executable, "Executable must not be null");
        this.executable = executable;
        this.parameterIndex = validateIndex(executable, parameterIndex);
        this.nestingLevel = 1;
        this.containingClass = containingClass;
    }


    /**
     * Return the wrapped Method, if any.
     * <p>Note: Either Method or Constructor is available.
     *
     * @return the Method, or {@code null} if none
     */
    @Nullable
    public Method getMethod() {
        return (this.executable instanceof Method ? (Method) this.executable : null);
    }

    /**
     * Return the wrapped Constructor, if any.
     * <p>Note: Either Method or Constructor is available.
     *
     * @return the Constructor, or {@code null} if none
     */
    @Nullable
    public Constructor<?> getConstructor() {
        return (this.executable instanceof Constructor ? (Constructor<?>) this.executable : null);
    }

    /**
     * Return the class that declares the underlying Method or Constructor.
     */
    public Class<?> getDeclaringClass() {
        return this.executable.getDeclaringClass();
    }

    /**
     * Return the wrapped member.
     *
     * @return the Method or Constructor as Member
     */
    public Member getMember() {
        return this.executable;
    }

    /**
     * Return the wrapped annotated element.
     * <p>Note: This method exposes the annotations declared on the method/constructor
     * itself (i.e. at the method/constructor level, not at the parameter level).
     *
     * @return the Method or Constructor as AnnotatedElement
     */
    public AnnotatedElement getAnnotatedElement() {
        return this.executable;
    }

    /**
     * Return the wrapped executable.
     *
     * @return the Method or Constructor as Executable
     * @since 5.0
     */
    public Executable getExecutable() {
        return this.executable;
    }

    /**
     * Return the {@link Parameter} descriptor for method/constructor parameter.
     *
     * @since 5.0
     */
    public Parameter getParameter() {
        if (this.parameterIndex < 0) {
            throw new IllegalStateException("Cannot retrieve Parameter descriptor for method return type");
        }
        Parameter parameter = this.parameter;
        if (parameter == null) {
            parameter = getExecutable().getParameters()[this.parameterIndex];
            this.parameter = parameter;
        }
        return parameter;
    }

    /**
     * Return the index of the method/constructor parameter.
     *
     * @return the parameter index (-1 in case of the return type)
     */
    public int getParameterIndex() {
        return this.parameterIndex;
    }

    @Deprecated
    public void increaseNestingLevel() {
        this.nestingLevel++;
    }

    @Deprecated
    public void decreaseNestingLevel() {
        getTypeIndexesPerLevel().remove(this.nestingLevel);
        this.nestingLevel--;
    }

    /**
     * Return the nesting level of the target type
     * (typically 1; e.g. in case of a List of Lists, 1 would indicate the
     * nested List, whereas 2 would indicate the element of the nested List).
     */
    public int getNestingLevel() {
        return this.nestingLevel;
    }


    @Deprecated
    public void setTypeIndexForCurrentLevel(int typeIndex) {
        getTypeIndexesPerLevel().put(this.nestingLevel, typeIndex);
    }

    /**
     * Return the type index for the current nesting level.
     *
     * @return the corresponding type index, or {@code null}
     * if none specified (indicating the default type index)
     * @see #getNestingLevel()
     */
    @Nullable
    public Integer getTypeIndexForCurrentLevel() {
        return getTypeIndexForLevel(this.nestingLevel);
    }

    /**
     * Return the type index for the specified nesting level.
     *
     * @param nestingLevel the nesting level to check
     * @return the corresponding type index, or {@code null}
     * if none specified (indicating the default type index)
     */
    @Nullable
    public Integer getTypeIndexForLevel(int nestingLevel) {
        return getTypeIndexesPerLevel().get(nestingLevel);
    }

    /**
     * Obtain the (lazily constructed) type-indexes-per-level Map.
     */
    private Map<Integer, Integer> getTypeIndexesPerLevel() {
        if (this.typeIndexesPerLevel == null) {
            this.typeIndexesPerLevel = new HashMap<>(4);
        }
        return this.typeIndexesPerLevel;
    }


    /**
     * Set a containing class to resolve the parameter type against.
     */
    @Deprecated
    void setContainingClass(Class<?> containingClass) {
        this.containingClass = containingClass;
        this.parameterType = null;
    }

    /**
     * Return the containing class for this method parameter.
     *
     * @return a specific containing class (potentially a subclass of the
     * declaring class), or otherwise simply the declaring class itself
     * @see #getDeclaringClass()
     */
    public Class<?> getContainingClass() {
        Class<?> containingClass = this.containingClass;
        return (containingClass != null ? containingClass : getDeclaringClass());
    }

    /**
     * Set a resolved (generic) parameter type.
     */
    @Deprecated
    void setParameterType(@Nullable Class<?> parameterType) {
        this.parameterType = parameterType;
    }


}

