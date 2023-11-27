package com.springframework.beans;

import com.springframework.core.convert.ConversionService;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class TypeConverterSupport extends PropertyEditorRegistrySupport implements TypeConverter{
    private ConversionService conversionService;

    private boolean defaultEditorsActive = false;

    private boolean configValueEditorsActive = false;

    protected void registerDefaultEditors() {
        this.defaultEditorsActive = true;
    }

}
