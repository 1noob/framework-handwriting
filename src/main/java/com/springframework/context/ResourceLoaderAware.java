package com.springframework.context;

import com.springframework.beans.factory.Aware;
import com.springframework.core.io.ResourceLoader;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface ResourceLoaderAware extends Aware {

    void setResourceLoader(ResourceLoader resourceLoader);

}
