package com.springframework.beans.factory;

/**
 * @author Gary
 */
public interface BeanNameAware extends Aware {

    void setBeanName(String name);

}
