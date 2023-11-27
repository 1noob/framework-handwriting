package com.springframework.beans.factory.config;

import com.springframework.util.Assert;
import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class BeanExpressionContext {
    private final ConfigurableBeanFactory beanFactory;

    @Nullable
    private final Scope scope;


    public BeanExpressionContext(ConfigurableBeanFactory beanFactory, @Nullable Scope scope) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
        this.scope = scope;
    }

    public final ConfigurableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }


    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BeanExpressionContext)) {
            return false;
        }
        BeanExpressionContext otherContext = (BeanExpressionContext) other;
        return (this.beanFactory == otherContext.beanFactory && this.scope == otherContext.scope);
    }

    @Override
    public int hashCode() {
        return this.beanFactory.hashCode();
    }
}
