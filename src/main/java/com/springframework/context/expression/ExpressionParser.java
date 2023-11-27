package com.springframework.context.expression;

import com.springframework.beans.factory.xml.ParserContext;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface ExpressionParser {
    Expression parseExpression(String var1) throws Exception;

    Expression parseExpression(String var1, ParserContext var2) throws Exception;

}
