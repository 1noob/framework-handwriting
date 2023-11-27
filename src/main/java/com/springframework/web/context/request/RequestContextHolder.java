package com.springframework.web.context.request;

import com.springframework.core.NamedInheritableThreadLocal;
import com.springframework.core.NamedThreadLocal;
import com.springframework.util.ClassUtils;
import com.sun.istack.internal.Nullable;

import javax.faces.context.FacesContext;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class RequestContextHolder {
    private static final boolean jsfPresent =
            ClassUtils.isPresent("javax.faces.context.FacesContext", RequestContextHolder.class.getClassLoader());

    private static final ThreadLocal<RequestAttributes> requestAttributesHolder =
            new NamedThreadLocal<>("Request attributes");
    private static final ThreadLocal<RequestAttributes> inheritableRequestAttributesHolder =
            new NamedInheritableThreadLocal<>("Request context");

    /**
     * Inner class to avoid hard-coded JSF dependency.
     */
    private static class FacesRequestAttributesFactory {

        @Nullable
        public static RequestAttributes getFacesRequestAttributes() {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            return (facesContext != null ? new FacesRequestAttributes(facesContext) : null);
        }
    }

    public static RequestAttributes getRequestAttributes() {
        RequestAttributes attributes = requestAttributesHolder.get();
        if (attributes == null) {
            attributes = inheritableRequestAttributesHolder.get();
        }
        return attributes;
    }

    public static RequestAttributes currentRequestAttributes() throws IllegalStateException {
        RequestAttributes attributes = getRequestAttributes();
        if (attributes == null) {
            if (jsfPresent) {
                attributes = FacesRequestAttributesFactory.getFacesRequestAttributes();
            }
            if (attributes == null) {
                throw new IllegalStateException("No thread-bound request found: " +
                        "Are you referring to request attributes outside of an actual web request, " +
                        "or processing a request outside of the originally receiving thread? " +
                        "If you are actually operating within a web request and still receive this message, " +
                        "your code is probably running outside of DispatcherServlet: " +
                        "In this case, use RequestContextListener or RequestContextFilter to expose the current request.");
            }
        }
        return attributes;
    }
}
