package com.springframework.beans;

import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class PropertyAccessorUtils {
    public static boolean isNestedOrIndexedProperty(@Nullable String propertyPath) {
        if (propertyPath == null) {
            return false;
        }
        for (int i = 0; i < propertyPath.length(); i++) {
            char ch = propertyPath.charAt(i);
            if (ch == PropertyAccessor.NESTED_PROPERTY_SEPARATOR_CHAR ||
                    ch == PropertyAccessor.PROPERTY_KEY_PREFIX_CHAR) {
                return true;
            }
        }
        return false;
    }
    public static int getLastNestedPropertySeparatorIndex(String propertyPath) {
        return getNestedPropertySeparatorIndex(propertyPath, true);
    }

    public static String getPropertyName(String propertyPath) {
        int separatorIndex = (propertyPath.endsWith(PropertyAccessor.PROPERTY_KEY_SUFFIX) ?
                propertyPath.indexOf(PropertyAccessor.PROPERTY_KEY_PREFIX_CHAR) : -1);
        return (separatorIndex != -1 ? propertyPath.substring(0, separatorIndex) : propertyPath);
    }
    public static int getFirstNestedPropertySeparatorIndex(String propertyPath) {
        return getNestedPropertySeparatorIndex(propertyPath, false);
    }
    private static int getNestedPropertySeparatorIndex(String propertyPath, boolean last) {
        boolean inKey = false;
        int length = propertyPath.length();
        int i = (last ? length - 1 : 0);
        while (last ? i >= 0 : i < length) {
            switch (propertyPath.charAt(i)) {
                case PropertyAccessor.PROPERTY_KEY_PREFIX_CHAR:
                case PropertyAccessor.PROPERTY_KEY_SUFFIX_CHAR:
                    inKey = !inKey;
                    break;
                case PropertyAccessor.NESTED_PROPERTY_SEPARATOR_CHAR:
                    if (!inKey) {
                        return i;
                    }
            }
            if (last) {
                i--;
            }
            else {
                i++;
            }
        }
        return -1;
    }
}
