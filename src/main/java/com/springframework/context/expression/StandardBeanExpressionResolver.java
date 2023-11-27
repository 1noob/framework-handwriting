package com.springframework.context.expression;

import com.springframework.beans.factory.config.BeanExpressionContext;
import com.springframework.beans.factory.config.BeanExpressionResolver;
import com.springframework.context.expression.spel.SpelParserConfiguration;
import com.springframework.context.expression.spel.standard.SpelExpressionParser;
import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class StandardBeanExpressionResolver implements BeanExpressionResolver {
    private ExpressionParser expressionParser;

    public StandardBeanExpressionResolver(ClassLoader beanClassLoader) {
    }


    @Override
    public Object evaluate(String value, BeanExpressionContext evalContext) throws RuntimeException {
        return null;
    }
}
