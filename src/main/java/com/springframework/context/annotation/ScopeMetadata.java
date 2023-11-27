package com.springframework.context.annotation;

import com.springframework.beans.config.BeanDefinition;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class ScopeMetadata {
    private String scopeName = BeanDefinition.SCOPE_SINGLETON;
    private ScopedProxyMode scopedProxyMode = ScopedProxyMode.NO;

    public String getScopeName() {
        return this.scopeName;
    }
    public ScopedProxyMode getScopedProxyMode() {
        return this.scopedProxyMode;
    }
}
