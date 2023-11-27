package com.springframework.context.annotation;

import com.springframework.beans.config.BeanDefinition;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
@FunctionalInterface
public interface ScopeMetadataResolver {


    ScopeMetadata resolveScopeMetadata(BeanDefinition definition);

}
