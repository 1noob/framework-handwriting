package com.springframework.beans;

import com.springframework.core.convert.ConversionService;
import com.springframework.util.ClassUtils;
import com.sun.istack.internal.Nullable;

import java.beans.PropertyEditor;
import java.util.Collection;
import java.util.Map;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class PropertyEditorRegistrySupport implements PropertyEditorRegistry {
    @Nullable
    private ConversionService conversionService;
    public ConversionService getConversionService() {
        return this.conversionService;
    }
    private boolean defaultEditorsActive = false;

    private boolean configValueEditorsActive = false;

    @Nullable
    private Map<Class<?>, PropertyEditor> defaultEditors;

    @Nullable
    private Map<Class<?>, PropertyEditor> overriddenDefaultEditors;

    @Nullable
    private Map<Class<?>, PropertyEditor> customEditors;


    @Nullable
    private Map<Class<?>, PropertyEditor> customEditorCache;
    @Nullable
    private Map<String, CustomEditorHolder> customEditorsForPath;

    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }
    protected void copyCustomEditorsTo(PropertyEditorRegistry target, @Nullable String nestedProperty) {
        String actualPropertyName =
                (nestedProperty != null ? PropertyAccessorUtils.getPropertyName(nestedProperty) : null);
        if (this.customEditors != null) {
            this.customEditors.forEach(target::registerCustomEditor);
        }
        if (this.customEditorsForPath != null) {
            this.customEditorsForPath.forEach((editorPath, editorHolder) -> {
                if (nestedProperty != null) {
                    int pos = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(editorPath);
                    if (pos != -1) {
                        String editorNestedProperty = editorPath.substring(0, pos);
                        String editorNestedPath = editorPath.substring(pos + 1);
                        if (editorNestedProperty.equals(nestedProperty) || editorNestedProperty.equals(actualPropertyName)) {
                            target.registerCustomEditor(
                                    editorHolder.getRegisteredType(), editorNestedPath, editorHolder.getPropertyEditor());
                        }
                    }
                }
                else {
                    target.registerCustomEditor(
                            editorHolder.getRegisteredType(), editorPath, editorHolder.getPropertyEditor());
                }
            });
        }
    }

    @Override
    public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {

    }

    @Override
    public void registerCustomEditor(Class<?> requiredType, String propertyPath, PropertyEditor propertyEditor) {

    }

    private static final class CustomEditorHolder {

        private final PropertyEditor propertyEditor;

        @Nullable
        private final Class<?> registeredType;

        private CustomEditorHolder(PropertyEditor propertyEditor, @Nullable Class<?> registeredType) {
            this.propertyEditor = propertyEditor;
            this.registeredType = registeredType;
        }

        private PropertyEditor getPropertyEditor() {
            return this.propertyEditor;
        }

        @Nullable
        private Class<?> getRegisteredType() {
            return this.registeredType;
        }

        @Nullable
        private PropertyEditor getPropertyEditor(@Nullable Class<?> requiredType) {
            // Special case: If no required type specified, which usually only happens for
            // Collection elements, or required type is not assignable to registered type,
            // which usually only happens for generic properties of type Object -
            // then return PropertyEditor if not registered for Collection or array type.
            // (If not registered for Collection or array, it is assumed to be intended
            // for elements.)
            if (this.registeredType == null ||
                    (requiredType != null &&
                            (ClassUtils.isAssignable(this.registeredType, requiredType) ||
                                    ClassUtils.isAssignable(requiredType, this.registeredType))) ||
                    (requiredType == null &&
                            (!Collection.class.isAssignableFrom(this.registeredType) && !this.registeredType.isArray()))) {
                return this.propertyEditor;
            }
            else {
                return null;
            }
        }
    }
    protected void copyDefaultEditorsTo(PropertyEditorRegistrySupport target) {
        target.defaultEditorsActive = this.defaultEditorsActive;
        target.configValueEditorsActive = this.configValueEditorsActive;
        target.defaultEditors = this.defaultEditors;
        target.overriddenDefaultEditors = this.overriddenDefaultEditors;
    }
}
