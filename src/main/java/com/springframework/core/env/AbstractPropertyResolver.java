package com.springframework.core.env;

import com.springframework.util.PropertyPlaceholderHelper;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class AbstractPropertyResolver implements ConfigurablePropertyResolver {
    private PropertyPlaceholderHelper strictHelper;
    private String placeholderPrefix = "${";
    private String placeholderSuffix = "}";
    private String valueSeparator = ":";

    @Override
    public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
        if (this.strictHelper == null) {
            this.strictHelper = this.createPlaceholderHelper(false);
        }

        return this.doResolvePlaceholders(text, this.strictHelper);
    }
    private PropertyPlaceholderHelper createPlaceholderHelper(boolean ignoreUnresolvablePlaceholders) {
        return new PropertyPlaceholderHelper(this.placeholderPrefix, this.placeholderSuffix, this.valueSeparator, ignoreUnresolvablePlaceholders);
    }

    private String doResolvePlaceholders(String text, PropertyPlaceholderHelper helper) {
        return helper.replacePlaceholders(text, this::getPropertyAsRawString);
    }
    protected abstract String getPropertyAsRawString(String var1);
}
