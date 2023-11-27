package com.springframework.beans;

import com.sun.istack.internal.Nullable;

import java.beans.PropertyEditor;

public interface PropertyEditorRegistry {
    void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor);
    void registerCustomEditor(@Nullable Class<?> requiredType, @Nullable String propertyPath, PropertyEditor propertyEditor);

}
