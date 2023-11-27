package com.springframework.web.context.support;

import com.springframework.beans.factory.ObjectFactory;
import com.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.springframework.util.ClassUtils;
import com.springframework.web.context.ConfigurableWebApplicationContext;
import com.springframework.web.context.WebApplicationContext;
import com.springframework.web.context.request.*;
import com.sun.istack.internal.Nullable;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class WebApplicationContextUtils {
    private static final boolean jsfPresent =
            ClassUtils.isPresent("javax.faces.context.FacesContext", RequestContextHolder.class.getClassLoader());


    public static void registerWebApplicationScopes(ConfigurableListableBeanFactory beanFactory,
                                                    @Nullable ServletContext sc) {

        beanFactory.registerScope(WebApplicationContext.SCOPE_REQUEST, new RequestScope());
        beanFactory.registerScope(WebApplicationContext.SCOPE_SESSION, new SessionScope());
        if (sc != null) {
            ServletContextScope appScope = new ServletContextScope(sc);
            beanFactory.registerScope(WebApplicationContext.SCOPE_APPLICATION, appScope);
            // Register as ServletContext attribute, for ContextCleanupListener to detect it.
            sc.setAttribute(ServletContextScope.class.getName(), appScope);
        }

        beanFactory.registerResolvableDependency(ServletRequest.class, new RequestObjectFactory());
        beanFactory.registerResolvableDependency(ServletResponse.class, new ResponseObjectFactory());
        beanFactory.registerResolvableDependency(HttpSession.class, new SessionObjectFactory());
        beanFactory.registerResolvableDependency(WebRequest.class, new WebRequestObjectFactory());
        if (jsfPresent) {
            FacesDependencyRegistrar.registerFacesDependencies(beanFactory);
        }
    }

    /**
     * Inner class to avoid hard-coded JSF dependency.
     */
    private static class FacesDependencyRegistrar {

        public static void registerFacesDependencies(ConfigurableListableBeanFactory beanFactory) {
            beanFactory.registerResolvableDependency(FacesContext.class, new ObjectFactory<FacesContext>() {
                @Override
                public FacesContext getObject() {
                    return FacesContext.getCurrentInstance();
                }

                @Override
                public String toString() {
                    return "Current JSF FacesContext";
                }
            });
            beanFactory.registerResolvableDependency(ExternalContext.class, new ObjectFactory<ExternalContext>() {
                @Override
                public ExternalContext getObject() {
                    return FacesContext.getCurrentInstance().getExternalContext();
                }

                @Override
                public String toString() {
                    return "Current JSF ExternalContext";
                }
            });
        }
    }

    private static class WebRequestObjectFactory implements ObjectFactory<WebRequest>, Serializable {

        @Override
        public WebRequest getObject() {
            ServletRequestAttributes requestAttr = currentRequestAttributes();
            return new ServletWebRequest(requestAttr.getRequest(), requestAttr.getResponse());
        }

        @Override
        public String toString() {
            return "Current ServletWebRequest";
        }
    }

    private static class SessionObjectFactory implements ObjectFactory<HttpSession>, Serializable {

        @Override
        public HttpSession getObject() {
            return currentRequestAttributes().getRequest().getSession();
        }

        @Override
        public String toString() {
            return "Current HttpSession";
        }
    }

    private static class ResponseObjectFactory implements ObjectFactory<ServletResponse>, Serializable {

        @Override
        public ServletResponse getObject() {
            ServletResponse response = currentRequestAttributes().getResponse();
            if (response == null) {
                throw new IllegalStateException("Current servlet response not available - " +
                        "consider using RequestContextFilter instead of RequestContextListener");
            }
            return response;
        }

        @Override
        public String toString() {
            return "Current HttpServletResponse";
        }
    }

    private static ServletRequestAttributes currentRequestAttributes() {
        RequestAttributes requestAttr = RequestContextHolder.currentRequestAttributes();
        if (!(requestAttr instanceof ServletRequestAttributes)) {
            throw new IllegalStateException("Current request is not a servlet request");
        }
        return (ServletRequestAttributes) requestAttr;
    }

    private static class RequestObjectFactory implements ObjectFactory<ServletRequest>, Serializable {

        @Override
        public ServletRequest getObject() {
            return currentRequestAttributes().getRequest();
        }

        @Override
        public String toString() {
            return "Current HttpServletRequest";
        }
    }

    public static void registerEnvironmentBeans(ConfigurableListableBeanFactory bf,
                                                @Nullable ServletContext servletContext, @Nullable ServletConfig servletConfig) {

        if (servletContext != null && !bf.containsBean(WebApplicationContext.SERVLET_CONTEXT_BEAN_NAME)) {
            bf.registerSingleton(WebApplicationContext.SERVLET_CONTEXT_BEAN_NAME, servletContext);
        }

        if (servletConfig != null && !bf.containsBean(ConfigurableWebApplicationContext.SERVLET_CONFIG_BEAN_NAME)) {
            bf.registerSingleton(ConfigurableWebApplicationContext.SERVLET_CONFIG_BEAN_NAME, servletConfig);
        }

        if (!bf.containsBean(WebApplicationContext.CONTEXT_PARAMETERS_BEAN_NAME)) {
            Map<String, String> parameterMap = new HashMap<>();
            if (servletContext != null) {
                Enumeration<?> paramNameEnum = servletContext.getInitParameterNames();
                while (paramNameEnum.hasMoreElements()) {
                    String paramName = (String) paramNameEnum.nextElement();
                    parameterMap.put(paramName, servletContext.getInitParameter(paramName));
                }
            }
            if (servletConfig != null) {
                Enumeration<?> paramNameEnum = servletConfig.getInitParameterNames();
                while (paramNameEnum.hasMoreElements()) {
                    String paramName = (String) paramNameEnum.nextElement();
                    parameterMap.put(paramName, servletConfig.getInitParameter(paramName));
                }
            }
            bf.registerSingleton(WebApplicationContext.CONTEXT_PARAMETERS_BEAN_NAME,
                    Collections.unmodifiableMap(parameterMap));
        }

        if (!bf.containsBean(WebApplicationContext.CONTEXT_ATTRIBUTES_BEAN_NAME)) {
            Map<String, Object> attributeMap = new HashMap<>();
            if (servletContext != null) {
                Enumeration<?> attrNameEnum = servletContext.getAttributeNames();
                while (attrNameEnum.hasMoreElements()) {
                    String attrName = (String) attrNameEnum.nextElement();
                    attributeMap.put(attrName, servletContext.getAttribute(attrName));
                }
            }
            bf.registerSingleton(WebApplicationContext.CONTEXT_ATTRIBUTES_BEAN_NAME,
                    Collections.unmodifiableMap(attributeMap));
        }
    }

}
