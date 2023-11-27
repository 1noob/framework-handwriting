package com.springframework.context.expression.spel.standard;

import com.springframework.beans.factory.xml.ParserContext;
import com.springframework.context.expression.Expression;
import com.springframework.context.expression.common.TemplateAwareExpressionParser;
import com.springframework.context.expression.spel.SpelParserConfiguration;
import com.springframework.util.Assert;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class SpelExpressionParser extends TemplateAwareExpressionParser {

    private final SpelParserConfiguration configuration;


    public SpelExpressionParser(SpelParserConfiguration configuration) {
        Assert.notNull(configuration, "SpelParserConfiguration must not be null");
        this.configuration = configuration;
    }

    @Override
    public Expression parseExpression(String var1) throws Exception {
        return null;
    }

    @Override
    public Expression parseExpression(String var1, ParserContext var2) throws Exception {
        return null;
    }
}
