package com.springframework.beans;

import com.springframework.util.ObjectUtils;

import java.beans.PropertyDescriptor;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class PropertyDescriptorUtils {
    public static boolean equals(PropertyDescriptor pd, PropertyDescriptor otherPd) {
        return (ObjectUtils.nullSafeEquals(pd.getReadMethod(), otherPd.getReadMethod()) &&
                ObjectUtils.nullSafeEquals(pd.getWriteMethod(), otherPd.getWriteMethod()) &&
                ObjectUtils.nullSafeEquals(pd.getPropertyType(), otherPd.getPropertyType()) &&
                ObjectUtils.nullSafeEquals(pd.getPropertyEditorClass(), otherPd.getPropertyEditorClass()) &&
                pd.isBound() == otherPd.isBound() && pd.isConstrained() == otherPd.isConstrained());
    }


}
