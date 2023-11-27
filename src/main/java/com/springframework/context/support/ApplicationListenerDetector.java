package com.springframework.context.support;

import com.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import com.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
class ApplicationListenerDetector implements DestructionAwareBeanPostProcessor, MergedBeanDefinitionPostProcessor {
    private static final Log logger = LogFactory.getLog(ApplicationListenerDetector.class);

    private final transient AbstractApplicationContext applicationContext;

    private final transient Map<String, Boolean> singletonNames = new ConcurrentHashMap<>(256);


    public ApplicationListenerDetector(AbstractApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}
