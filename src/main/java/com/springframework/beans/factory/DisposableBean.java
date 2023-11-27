package com.springframework.beans.factory;

/**
 * @author Gary
 */
public interface DisposableBean {
    void destroy() throws Exception;
}
