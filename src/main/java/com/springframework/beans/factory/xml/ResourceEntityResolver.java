package com.springframework.beans.factory.xml;

import com.springframework.core.io.ResourceLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class ResourceEntityResolver extends DelegatingEntityResolver{
    private static final Log logger = LogFactory.getLog(ResourceEntityResolver.class);
    /** 资源加载器 */
    private final ResourceLoader resourceLoader;
    public ResourceEntityResolver(ResourceLoader resourceLoader) {
        super(resourceLoader.getClassLoader());
        this.resourceLoader = resourceLoader;
    }
}
