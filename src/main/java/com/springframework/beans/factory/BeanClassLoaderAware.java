package com.springframework.beans.factory;

/**
 * @author Gary
 */
public interface BeanClassLoaderAware extends Aware {


    void setBeanClassLoader(ClassLoader classLoader);

}
