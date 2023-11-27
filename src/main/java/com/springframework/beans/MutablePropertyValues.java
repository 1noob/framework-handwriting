package com.springframework.beans;

import com.sun.istack.internal.Nullable;

import java.util.*;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class MutablePropertyValues implements PropertyValues {
    public MutablePropertyValues add(String propertyName, @Nullable Object propertyValue) {
        addPropertyValue(new PropertyValue(propertyName, propertyValue));
        return this;
    }
    public void setConverted() {
        this.converted = true;
    }

    public List<PropertyValue> getPropertyValueList() {
        return this.propertyValueList;
    }
    private final List<PropertyValue> propertyValueList;
    public boolean isConverted() {
        return this.converted;
    }
    @Nullable
    private Set<String> processedProperties;

    private volatile boolean converted = false;

    @Override
    public boolean contains(String propertyName) {
        return (getPropertyValue(propertyName) != null ||
                (this.processedProperties != null && this.processedProperties.contains(propertyName)));
    }

    @Override
    @Nullable
    public PropertyValue getPropertyValue(String propertyName) {
        for (PropertyValue pv : this.propertyValueList) {
            if (pv.getName().equals(propertyName)) {
                return pv;
            }
        }
        return null;
    }

    public MutablePropertyValues() {
        this.propertyValueList = new ArrayList<>(0);
    }
    public MutablePropertyValues(@Nullable List<PropertyValue> propertyValueList) {
        this.propertyValueList =
                (propertyValueList != null ? propertyValueList : new ArrayList<>());
    }
    public MutablePropertyValues(PropertyValues original) {
        // We can optimize this because it's all new:
        // There is no replacement of existing property values.
        if (original != null) {
            PropertyValue[] pvs = original.getPropertyValues();
            this.propertyValueList = new ArrayList<>(pvs.length);
            for (PropertyValue pv : pvs) {
                this.propertyValueList.add(new PropertyValue(pv));
            }
        } else {
            this.propertyValueList = new ArrayList<>(0);
        }
    }


    public MutablePropertyValues addPropertyValues(PropertyValues other) {
        if (other != null) {
            PropertyValue[] pvs = other.getPropertyValues();
            for (PropertyValue pv : pvs) {
                addPropertyValue(new PropertyValue(pv));
            }
        }
        return this;
    }

    private PropertyValue mergeIfRequired(PropertyValue newPv, PropertyValue currentPv) {
        Object value = newPv.getValue();
        if (value instanceof Mergeable) {
            Mergeable mergeable = (Mergeable) value;
            if (mergeable.isMergeEnabled()) {
                Object merged = mergeable.merge(currentPv.getValue());
                return new PropertyValue(newPv.getName(), merged);
            }
        }
        return newPv;
    }

    public void setPropertyValueAt(PropertyValue pv, int i) {
        this.propertyValueList.set(i, pv);
    }

    public MutablePropertyValues addPropertyValue(PropertyValue pv) {
        for (int i = 0; i < this.propertyValueList.size(); i++) {
            PropertyValue currentPv = this.propertyValueList.get(i);
            if (currentPv.getName().equals(pv.getName())) {
                pv = mergeIfRequired(pv, currentPv);
                setPropertyValueAt(pv, i);
                return this;
            }
        }
        this.propertyValueList.add(pv);
        return this;
    }

    @Override
    public PropertyValue[] getPropertyValues() {
        return this.propertyValueList.toArray(new PropertyValue[0]);
    }

    @Override
    public Iterator<PropertyValue> iterator() {
        return Collections.unmodifiableList(this.propertyValueList).iterator();
    }

    @Override
    public boolean isEmpty() {
        return this.propertyValueList.isEmpty();
    }
}
