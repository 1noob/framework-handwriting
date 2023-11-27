package com.springframework.beans;


import com.springframework.core.convert.ConversionService;
import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface ConfigurablePropertyAccessor extends PropertyAccessor, PropertyEditorRegistry, TypeConverter {
    void setConversionService(@Nullable ConversionService conversionService);
    boolean isExtractOldValueForEditor();
    void setExtractOldValueForEditor(boolean extractOldValueForEditor);
    void setAutoGrowNestedPaths(boolean autoGrowNestedPaths);

    void setPropertyValues2(PropertyValues var1) throws RuntimeException;
    boolean isAutoGrowNestedPaths();
}
