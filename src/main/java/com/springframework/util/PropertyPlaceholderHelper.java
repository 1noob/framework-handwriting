package com.springframework.util;

import com.sun.istack.internal.Nullable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class PropertyPlaceholderHelper {
    private final String placeholderPrefix;
    private final String placeholderSuffix;
    private final String simplePrefix;
    private final String valueSeparator;
    private final boolean ignoreUnresolvablePlaceholders;
    private static final Map<String, String> wellKnownSimplePrefixes = new HashMap(4);
    private static final Log logger = LogFactory.getLog(PropertyPlaceholderHelper.class);

    public PropertyPlaceholderHelper(String placeholderPrefix, String placeholderSuffix, @Nullable String valueSeparator, boolean ignoreUnresolvablePlaceholders) {
        Assert.notNull(placeholderPrefix, "'placeholderPrefix' must not be null");
        Assert.notNull(placeholderSuffix, "'placeholderSuffix' must not be null");
        this.placeholderPrefix = placeholderPrefix;
        this.placeholderSuffix = placeholderSuffix;
        String simplePrefixForSuffix = (String) wellKnownSimplePrefixes.get(this.placeholderSuffix);
        if (simplePrefixForSuffix != null && this.placeholderPrefix.endsWith(simplePrefixForSuffix)) {
            this.simplePrefix = simplePrefixForSuffix;
        } else {
            this.simplePrefix = this.placeholderPrefix;
        }

        this.valueSeparator = valueSeparator;
        this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
    }

    public String replacePlaceholders(String value, PropertyPlaceholderHelper.PlaceholderResolver placeholderResolver) {
        Assert.notNull(value, "'value' must not be null");
        return this.parseStringValue(value, placeholderResolver, (Set)null);
    }
    protected String parseStringValue(String value, PropertyPlaceholderHelper.PlaceholderResolver placeholderResolver, @Nullable Set<String> visitedPlaceholders) {
        int startIndex = value.indexOf(this.placeholderPrefix);
        if (startIndex == -1) {
            return value;
        } else {
            StringBuilder result = new StringBuilder(value);

            while(startIndex != -1) {
                int endIndex = this.findPlaceholderEndIndex(result, startIndex);
                if (endIndex != -1) {
                    String placeholder = result.substring(startIndex + this.placeholderPrefix.length(), endIndex);
                    String originalPlaceholder = placeholder;
                    if (visitedPlaceholders == null) {
                        visitedPlaceholders = new HashSet(4);
                    }

                    if (!((Set)visitedPlaceholders).add(placeholder)) {
                        throw new IllegalArgumentException("Circular placeholder reference '" + placeholder + "' in property definitions");
                    }

                    placeholder = this.parseStringValue(placeholder, placeholderResolver, (Set)visitedPlaceholders);
                    String propVal = placeholderResolver.resolvePlaceholder(placeholder);
                    if (propVal == null && this.valueSeparator != null) {
                        int separatorIndex = placeholder.indexOf(this.valueSeparator);
                        if (separatorIndex != -1) {
                            String actualPlaceholder = placeholder.substring(0, separatorIndex);
                            String defaultValue = placeholder.substring(separatorIndex + this.valueSeparator.length());
                            propVal = placeholderResolver.resolvePlaceholder(actualPlaceholder);
                            if (propVal == null) {
                                propVal = defaultValue;
                            }
                        }
                    }

                    if (propVal != null) {
                        propVal = this.parseStringValue(propVal, placeholderResolver, (Set)visitedPlaceholders);
                        result.replace(startIndex, endIndex + this.placeholderSuffix.length(), propVal);
                        if (logger.isTraceEnabled()) {
                            logger.trace("Resolved placeholder '" + placeholder + "'");
                        }

                        startIndex = result.indexOf(this.placeholderPrefix, startIndex + propVal.length());
                    } else {
                        if (!this.ignoreUnresolvablePlaceholders) {
                            throw new IllegalArgumentException("Could not resolve placeholder '" + placeholder + "' in value \"" + value + "\"");
                        }

                        startIndex = result.indexOf(this.placeholderPrefix, endIndex + this.placeholderSuffix.length());
                    }

                    ((Set)visitedPlaceholders).remove(originalPlaceholder);
                } else {
                    startIndex = -1;
                }
            }

            return result.toString();
        }
    }
    private int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
        int index = startIndex + this.placeholderPrefix.length();
        int withinNestedPlaceholder = 0;

        while(index < buf.length()) {
            if (StringUtils.substringMatch(buf, index, this.placeholderSuffix)) {
                if (withinNestedPlaceholder <= 0) {
                    return index;
                }

                --withinNestedPlaceholder;
                index += this.placeholderSuffix.length();
            } else if (StringUtils.substringMatch(buf, index, this.simplePrefix)) {
                ++withinNestedPlaceholder;
                index += this.simplePrefix.length();
            } else {
                ++index;
            }
        }

        return -1;
    }
    @FunctionalInterface
    public interface PlaceholderResolver {
        @Nullable
        String resolvePlaceholder(String var1);
    }
}
