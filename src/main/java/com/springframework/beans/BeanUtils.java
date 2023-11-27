package com.springframework.beans;

import com.springframework.core.KotlinDetector;
import com.springframework.core.MethodParameter;
import com.springframework.util.Assert;
import com.springframework.util.ClassUtils;
import com.springframework.util.ConcurrentReferenceHashMap;
import com.springframework.util.ReflectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.time.temporal.Temporal;
import java.util.*;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class BeanUtils {
    public static MethodParameter getWriteMethodParameter(PropertyDescriptor pd) {
        if (pd instanceof GenericTypeAwarePropertyDescriptor) {
            return new MethodParameter(((GenericTypeAwarePropertyDescriptor) pd).getWriteMethodParameter());
        }
        else {
            Method writeMethod = pd.getWriteMethod();
            Assert.state(writeMethod != null, "No write method available");
            return new MethodParameter(writeMethod, 0);
        }
    }
    public static boolean isSimpleProperty(Class<?> type) {
        Assert.notNull(type, "'type' must not be null");
        return isSimpleValueType(type) || (type.isArray() && isSimpleValueType(type.getComponentType()));
    }
    public static boolean isSimpleValueType(Class<?> type) {
        return (Void.class != type && void.class != type &&
                (ClassUtils.isPrimitiveOrWrapper(type) ||
                        Enum.class.isAssignableFrom(type) ||
                        CharSequence.class.isAssignableFrom(type) ||
                        Number.class.isAssignableFrom(type) ||
                        Date.class.isAssignableFrom(type) ||
                        Temporal.class.isAssignableFrom(type) ||
                        URI.class == type ||
                        URL.class == type ||
                        Locale.class == type ||
                        Class.class == type));
    }
    private static final Log logger = LogFactory.getLog(BeanUtils.class);
    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        try {
            return clazz.getMethod(methodName, paramTypes);
        }
        catch (NoSuchMethodException ex) {
            return findDeclaredMethod(clazz, methodName, paramTypes);
        }
    }
    public static Method findDeclaredMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, paramTypes);
        }
        catch (NoSuchMethodException ex) {
            if (clazz.getSuperclass() != null) {
                return findDeclaredMethod(clazz.getSuperclass(), methodName, paramTypes);
            }
            return null;
        }
    }
    private static final Set<Class<?>> unknownEditorTypes =
            Collections.newSetFromMap(new ConcurrentReferenceHashMap<>(64));

    private static final Map<Class<?>, Object> DEFAULT_TYPE_VALUES;

    static {
        Map<Class<?>, Object> values = new HashMap<>();
        values.put(boolean.class, false);
        values.put(byte.class, (byte) 0);
        values.put(short.class, (short) 0);
        values.put(int.class, 0);
        values.put(long.class, (long) 0);
        DEFAULT_TYPE_VALUES = Collections.unmodifiableMap(values);
    }

    public static <T> T instantiateClass(Class<T> clazz) throws RuntimeException {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isInterface()) {
            throw new RuntimeException(clazz + "=Specified class is an interface");
        }
        try {
            return instantiateClass(clazz.getDeclaredConstructor());
        } catch (Exception ex) {
            throw new RuntimeException(clazz + "No default constructor found", ex);
        } catch (LinkageError err) {
            throw new RuntimeException(clazz + "Unresolvable class definition", err);
        }
    }

    public static <T> T instantiateClass(Constructor<T> ctor, Object... args) throws RuntimeException {
        Assert.notNull(ctor, "Constructor must not be null");
        try {
            ReflectionUtils.makeAccessible(ctor);
            if (KotlinDetector.isKotlinReflectPresent() && KotlinDetector.isKotlinType(ctor.getDeclaringClass())) {
                return null;
//                return KotlinDelegate.instantiateClass(ctor, args);
            } else {
                Class<?>[] parameterTypes = ctor.getParameterTypes();
                Assert.isTrue(args.length <= parameterTypes.length, "Can't specify more arguments than constructor parameters");
                Object[] argsWithDefaultValues = new Object[args.length];
                for (int i = 0; i < args.length; i++) {
                    if (args[i] == null) {
                        Class<?> parameterType = parameterTypes[i];
                        argsWithDefaultValues[i] = (parameterType.isPrimitive() ? DEFAULT_TYPE_VALUES.get(parameterType) : null);
                    } else {
                        argsWithDefaultValues[i] = args[i];
                    }
                }
                return ctor.newInstance(argsWithDefaultValues);
            }
        } catch (InstantiationException ex) {
            throw new RuntimeException(ctor + "Is it an abstract class?", ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ctor + "Is the constructor accessible?", ex);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ctor + "Illegal arguments for constructor", ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ctor + "Constructor threw exception", ex.getTargetException());
        }
    }

}
