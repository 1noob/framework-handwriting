package com.springframework.context.event;

import com.springframework.aop.support.AopUtils;
import com.springframework.context.ApplicationContext;
import com.springframework.context.ApplicationEvent;
import com.springframework.context.expression.AnnotatedElementKey;
import com.springframework.core.BridgeMethodResolver;
import com.springframework.core.ResolvableType;
import com.springframework.core.annotation.AnnotatedElementUtils;
import com.springframework.core.annotation.Order;
import com.sun.istack.internal.Nullable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class ApplicationListenerMethodAdapter implements GenericApplicationListener {
    protected final Log logger = LogFactory.getLog(getClass());

    private final String beanName;

    private final Method method;

    private final Method targetMethod;
    private final int order;

    @Nullable
    private ApplicationContext applicationContext;

    private final AnnotatedElementKey methodKey;
    private final String condition;
    private final List<ResolvableType> declaredEventTypes;

    public ApplicationListenerMethodAdapter(String beanName, Class<?> targetClass, Method method) {
        this.beanName = beanName;
        this.method = BridgeMethodResolver.findBridgedMethod(method);
        this.targetMethod = (!Proxy.isProxyClass(targetClass) ?
                AopUtils.getMostSpecificMethod(method, targetClass) : this.method);
        this.methodKey = new AnnotatedElementKey(this.targetMethod, targetClass);

        EventListener ann = AnnotatedElementUtils.findMergedAnnotation(this.targetMethod, EventListener.class);
        this.declaredEventTypes = resolveDeclaredEventTypes(method, ann);
        this.condition = (ann != null ? ann.condition() : null);
        this.order = resolveOrder(this.targetMethod);
    }
    private static List<ResolvableType> resolveDeclaredEventTypes(Method method, @Nullable EventListener ann) {
        int count = method.getParameterCount();
        if (count > 1) {
            throw new IllegalStateException(
                    "Maximum one parameter is allowed for event listener method: " + method);
        }

        if (ann != null) {
            Class<?>[] classes = ann.classes();
            if (classes.length > 0) {
                List<ResolvableType> types = new ArrayList<>(classes.length);
                for (Class<?> eventType : classes) {
                    types.add(ResolvableType.forClass(eventType));
                }
                return types;
            }
        }

        if (count == 0) {
            throw new IllegalStateException(
                    "Event parameter is mandatory for event listener method: " + method);
        }
        return Collections.singletonList(ResolvableType.forMethodParameter(method, 0));
    }
    private static int resolveOrder(Method method) {
        Order ann = AnnotatedElementUtils.findMergedAnnotation(method, Order.class);
        return (ann != null ? ann.value() : 0);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

    }

    @Override
    public int getOrder() {
        return 0;
    }
}
