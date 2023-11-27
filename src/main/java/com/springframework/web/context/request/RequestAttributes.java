package com.springframework.web.context.request;

public interface RequestAttributes {
    int SCOPE_REQUEST = 0;
    Object getAttribute(String name, int scope);
    void setAttribute(String name, Object value, int scope);

}
