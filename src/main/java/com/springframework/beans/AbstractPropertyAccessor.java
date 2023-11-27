package com.springframework.beans;

import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class AbstractPropertyAccessor extends TypeConverterSupport implements ConfigurablePropertyAccessor {
    private boolean extractOldValueForEditor = false;
    @Override
    public boolean isExtractOldValueForEditor() {
        return this.extractOldValueForEditor;
    }

    private boolean autoGrowNestedPaths = false;
    @Override
    public void setPropertyValues(PropertyValues pvs) throws RuntimeException {
        this.setPropertyValues(pvs, false, false);
    }
    @Override
    public void setExtractOldValueForEditor(boolean extractOldValueForEditor) {
        this.extractOldValueForEditor = extractOldValueForEditor;
    }
    @Override
    public boolean isAutoGrowNestedPaths() {
        return this.autoGrowNestedPaths;
    }
    @Override
    public void setPropertyValue(PropertyValue pv) throws RuntimeException {
        setPropertyValue(pv.getName(), pv.getValue());
    }
    @Override
    public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown, boolean ignoreInvalid) throws RuntimeException {
        List<PropertyValue> propertyValues = pvs instanceof MutablePropertyValues ? ((MutablePropertyValues) pvs).getPropertyValueList() : Arrays.asList(pvs.getPropertyValues());
        Iterator var6 = propertyValues.iterator();

        while (var6.hasNext()) {
            PropertyValue pv = (PropertyValue) var6.next();

            try {
                this.setPropertyValue2(pv);
            } catch (Exception var9) {
                throw new RuntimeException(var9);
            }

        }
    }

    public void setPropertyValue2(PropertyValue pv) throws Exception {
        this.setPropertyValue2(pv.getName(), pv.getValue());
    }

    public abstract void setPropertyValue2(String propertyName, @Nullable Object value) throws RuntimeException;

}
