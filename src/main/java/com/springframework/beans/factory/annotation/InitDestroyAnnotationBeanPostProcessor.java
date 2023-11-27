package com.springframework.beans.factory.annotation;

import com.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import com.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import com.springframework.core.PriorityOrdered;

import java.io.Serializable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class InitDestroyAnnotationBeanPostProcessor
        implements DestructionAwareBeanPostProcessor, MergedBeanDefinitionPostProcessor, PriorityOrdered, Serializable {

    @Override
    public int getOrder() {
        return 0;
    }
}
