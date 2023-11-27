package com.springframework.web.context;

import com.springframework.context.ApplicationContext;

/**
 * @author Gary
 */
public interface WebApplicationContext extends ApplicationContext {
    String SCOPE_REQUEST = "request";
    String SCOPE_SESSION = "session";
    String SCOPE_APPLICATION = "application";

    String SERVLET_CONTEXT_BEAN_NAME = "servletContext";
    String CONTEXT_PARAMETERS_BEAN_NAME = "contextParameters";
    String CONTEXT_ATTRIBUTES_BEAN_NAME = "contextAttributes";
}
