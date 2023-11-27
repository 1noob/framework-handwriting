package com.springframework.core;

import com.springframework.util.ClassUtils;
import com.springframework.util.ConcurrentReferenceHashMap;
import com.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public final class BridgeMethodResolver {
    private static final Map<Method, Method> cache = new ConcurrentReferenceHashMap<>();

    private BridgeMethodResolver() {
    }
    private static boolean isBridgedCandidateFor(Method candidateMethod, Method bridgeMethod) {
        return (!candidateMethod.isBridge() && !candidateMethod.equals(bridgeMethod) &&
                candidateMethod.getName().equals(bridgeMethod.getName()) &&
                candidateMethod.getParameterCount() == bridgeMethod.getParameterCount());
    }
    public static Method findBridgedMethod(Method bridgeMethod) {
        if (!bridgeMethod.isBridge()) {
            return bridgeMethod;
        }
        Method bridgedMethod = cache.get(bridgeMethod);
        if (bridgedMethod == null) {
            // Gather all methods with matching name and parameter size.
            List<Method> candidateMethods = new ArrayList<>();
            ReflectionUtils.MethodFilter filter = candidateMethod ->
                    isBridgedCandidateFor(candidateMethod, bridgeMethod);
            ReflectionUtils.doWithMethods(bridgeMethod.getDeclaringClass(), candidateMethods::add, filter);
            if (!candidateMethods.isEmpty()) {
                bridgedMethod = candidateMethods.size() == 1 ?
                        candidateMethods.get(0) :
                        searchCandidates(candidateMethods, bridgeMethod);
            }
            if (bridgedMethod == null) {
                // A bridge method was passed in but we couldn't find the bridged method.
                // Let's proceed with the passed-in method and hope for the best...
                bridgedMethod = bridgeMethod;
            }
            cache.put(bridgeMethod, bridgedMethod);
        }
        return bridgedMethod;
    }
    private static Method searchCandidates(List<Method> candidateMethods, Method bridgeMethod) {
        if (candidateMethods.isEmpty()) {
            return null;
        }
        Method previousMethod = null;
        boolean sameSig = true;
        for (Method candidateMethod : candidateMethods) {
            if (isBridgeMethodFor(bridgeMethod, candidateMethod, bridgeMethod.getDeclaringClass())) {
                return candidateMethod;
            }
            else if (previousMethod != null) {
                sameSig = sameSig &&
                        Arrays.equals(candidateMethod.getGenericParameterTypes(), previousMethod.getGenericParameterTypes());
            }
            previousMethod = candidateMethod;
        }
        return (sameSig ? candidateMethods.get(0) : null);
    }

    static boolean isBridgeMethodFor(Method bridgeMethod, Method candidateMethod, Class<?> declaringClass) {
        if (isResolvedTypeMatch(candidateMethod, bridgeMethod, declaringClass)) {
            return true;
        }
        Method method = findGenericDeclaration(bridgeMethod);
        return (method != null && isResolvedTypeMatch(method, candidateMethod, declaringClass));
    }
    private static boolean isResolvedTypeMatch(Method genericMethod, Method candidateMethod, Class<?> declaringClass) {
        Type[] genericParameters = genericMethod.getGenericParameterTypes();
        if (genericParameters.length != candidateMethod.getParameterCount()) {
            return false;
        }
        Class<?>[] candidateParameters = candidateMethod.getParameterTypes();
        for (int i = 0; i < candidateParameters.length; i++) {
            ResolvableType genericParameter = ResolvableType.forMethodParameter(genericMethod, i, declaringClass);
            Class<?> candidateParameter = candidateParameters[i];
            if (candidateParameter.isArray()) {
                // An array type: compare the component type.
                if (!candidateParameter.getComponentType().equals(genericParameter.getComponentType().toClass())) {
                    return false;
                }
            }
            // A non-array type: compare the type itself.
            if (!candidateParameter.equals(genericParameter.toClass())) {
                return false;
            }
        }
        return true;
    }
    private static Method searchForMatch(Class<?> type, Method bridgeMethod) {
        try {
            return type.getDeclaredMethod(bridgeMethod.getName(), bridgeMethod.getParameterTypes());
        }
        catch (NoSuchMethodException ex) {
            return null;
        }
    }
    private static Method findGenericDeclaration(Method bridgeMethod) {
        // Search parent types for method that has same signature as bridge.
        Class<?> superclass = bridgeMethod.getDeclaringClass().getSuperclass();
        while (superclass != null && Object.class != superclass) {
            Method method = searchForMatch(superclass, bridgeMethod);
            if (method != null && !method.isBridge()) {
                return method;
            }
            superclass = superclass.getSuperclass();
        }

        Class<?>[] interfaces = ClassUtils.getAllInterfacesForClass(bridgeMethod.getDeclaringClass());
        return searchInterfaces(interfaces, bridgeMethod);
    }
    private static Method searchInterfaces(Class<?>[] interfaces, Method bridgeMethod) {
        for (Class<?> ifc : interfaces) {
            Method method = searchForMatch(ifc, bridgeMethod);
            if (method != null && !method.isBridge()) {
                return method;
            }
            else {
                method = searchInterfaces(ifc.getInterfaces(), bridgeMethod);
                if (method != null) {
                    return method;
                }
            }
        }
        return null;
    }

}
