package com.springframework.core.io;

import com.springframework.util.ResourceUtils;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface ResourceLoader {
    /** Pseudo URL prefix for loading from the class path: "classpath:". */
    String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;

    Resource getResource(String location);
    ClassLoader getClassLoader();
}
