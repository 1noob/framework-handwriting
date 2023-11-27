package com.springframework.beans.factory.config;

import com.springframework.beans.BeanMetadataElement;
import com.springframework.beans.Mergeable;
import com.springframework.util.Assert;
import com.springframework.util.ClassUtils;
import com.springframework.util.ObjectUtils;
import com.sun.istack.internal.Nullable;

import java.util.*;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class ConstructorArgumentValues {
    private final Map<Integer, ValueHolder> indexedArgumentValues = new LinkedHashMap<>();
    public int getArgumentCount() {
        return (this.indexedArgumentValues.size() + this.genericArgumentValues.size());
    }
    private final List<ValueHolder> genericArgumentValues = new ArrayList<>();

    public ConstructorArgumentValues() {

    }
    public ValueHolder getIndexedArgumentValue(int index, @Nullable Class<?> requiredType, @Nullable String requiredName) {
        Assert.isTrue(index >= 0, "Index must not be negative");
        ValueHolder valueHolder = this.indexedArgumentValues.get(index);
        if (valueHolder != null &&
                (valueHolder.getType() == null ||
                        (requiredType != null && ClassUtils.matchesTypeName(requiredType, valueHolder.getType()))) &&
                (valueHolder.getName() == null || "".equals(requiredName) ||
                        (requiredName != null && requiredName.equals(valueHolder.getName())))) {
            return valueHolder;
        }
        return null;
    }
    public ValueHolder getArgumentValue(int index, @Nullable Class<?> requiredType, @Nullable String requiredName, @Nullable Set<ValueHolder> usedValueHolders) {
        Assert.isTrue(index >= 0, "Index must not be negative");
        ValueHolder valueHolder = getIndexedArgumentValue(index, requiredType, requiredName);
        if (valueHolder == null) {
            valueHolder = getGenericArgumentValue(requiredType, requiredName, usedValueHolders);
        }
        return valueHolder;
    }
    @Nullable
    public ValueHolder getGenericArgumentValue(@Nullable Class<?> requiredType, @Nullable String requiredName, @Nullable Set<ValueHolder> usedValueHolders) {
        for (ValueHolder valueHolder : this.genericArgumentValues) {
            if (usedValueHolders != null && usedValueHolders.contains(valueHolder)) {
                continue;
            }
            if (valueHolder.getName() != null && !"".equals(requiredName) &&
                    (requiredName == null || !valueHolder.getName().equals(requiredName))) {
                continue;
            }
            if (valueHolder.getType() != null &&
                    (requiredType == null || !ClassUtils.matchesTypeName(requiredType, valueHolder.getType()))) {
                continue;
            }
            if (requiredType != null && valueHolder.getType() == null && valueHolder.getName() == null &&
                    !ClassUtils.isAssignableValue(requiredType, valueHolder.getValue())) {
                continue;
            }
            return valueHolder;
        }
        return null;
    }
    public void addIndexedArgumentValue(int index, ValueHolder newValue) {
        Assert.isTrue(index >= 0, "Index must not be negative");
        Assert.notNull(newValue, "ValueHolder must not be null");
        addOrMergeIndexedArgumentValue(index, newValue);
    }
    public boolean hasIndexedArgumentValue(int index) {
        return this.indexedArgumentValues.containsKey(index);
    }
    public void addGenericArgumentValue(ValueHolder newValue) {
        Assert.notNull(newValue, "ValueHolder must not be null");
        if (!this.genericArgumentValues.contains(newValue)) {
            addOrMergeGenericArgumentValue(newValue);
        }
    }
    /**
     * Return if this holder does not contain any argument values,
     * neither indexed ones nor generic ones.
     */
    public boolean isEmpty() {
        return (this.indexedArgumentValues.isEmpty() && this.genericArgumentValues.isEmpty());
    }
    private void addOrMergeIndexedArgumentValue(Integer key, ValueHolder newValue) {
        ValueHolder currentValue = this.indexedArgumentValues.get(key);
        if (currentValue != null && newValue.getValue() instanceof Mergeable) {
            Mergeable mergeable = (Mergeable) newValue.getValue();
            if (mergeable.isMergeEnabled()) {
                newValue.setValue(mergeable.merge(currentValue.getValue()));
            }
        }
        this.indexedArgumentValues.put(key, newValue);
    }

    private void addOrMergeGenericArgumentValue(ValueHolder newValue) {
        if (newValue.getName() != null) {
            for (Iterator<ValueHolder> it = this.genericArgumentValues.iterator(); it.hasNext(); ) {
                ValueHolder currentValue = it.next();
                if (newValue.getName().equals(currentValue.getName())) {
                    if (newValue.getValue() instanceof Mergeable) {
                        Mergeable mergeable = (Mergeable) newValue.getValue();
                        if (mergeable.isMergeEnabled()) {
                            newValue.setValue(mergeable.merge(currentValue.getValue()));
                        }
                    }
                    it.remove();
                }
            }
        }
        this.genericArgumentValues.add(newValue);
    }

    public ConstructorArgumentValues(ConstructorArgumentValues original) {
        addArgumentValues(original);
    }

    public void addArgumentValues(ConstructorArgumentValues other) {
        if (other != null) {
            other.indexedArgumentValues.forEach(
                    (index, argValue) -> addOrMergeIndexedArgumentValue(index, argValue.copy())
            );
            other.genericArgumentValues.stream()
                    .filter(valueHolder -> !this.genericArgumentValues.contains(valueHolder))
                    .forEach(valueHolder -> addOrMergeGenericArgumentValue(valueHolder.copy()));
        }
    }


    /**
     * Holder for a constructor argument value, with an optional type
     * attribute indicating the target type of the actual constructor argument.
     */
    public static class ValueHolder implements BeanMetadataElement {

        private Object value;


        private String type;


        private String name;


        private Object source;

        private boolean converted = false;

        private Object convertedValue;

        /**
         * Create a new ValueHolder for the given value.
         *
         * @param value the argument value
         */
        public ValueHolder(Object value) {
            this.value = value;
        }

        /**
         * Create a new ValueHolder for the given value and type.
         *
         * @param value the argument value
         * @param type  the type of the constructor argument
         */
        public ValueHolder(Object value, String type) {
            this.value = value;
            this.type = type;
        }

        /**
         * Create a new ValueHolder for the given value, type and name.
         *
         * @param value the argument value
         * @param type  the type of the constructor argument
         * @param name  the name of the constructor argument
         */
        public ValueHolder(Object value, String type, String name) {
            this.value = value;
            this.type = type;
            this.name = name;
        }

        /**
         * Set the value for the constructor argument.
         */
        public void setValue(Object value) {
            this.value = value;
        }

        /**
         * Return the value for the constructor argument.
         */
        public Object getValue() {
            return this.value;
        }

        /**
         * Set the type of the constructor argument.
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * Return the type of the constructor argument.
         */
        public String getType() {
            return this.type;
        }

        /**
         * Set the name of the constructor argument.
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Return the name of the constructor argument.
         */
        public String getName() {
            return this.name;
        }

        /**
         * Set the configuration source {@code Object} for this metadata element.
         * <p>The exact type of the object will depend on the configuration mechanism used.
         */
        public void setSource(Object source) {
            this.source = source;
        }

        @Override
        public Object getSource() {
            return this.source;
        }

        /**
         * Return whether this holder contains a converted value already ({@code true}),
         * or whether the value still needs to be converted ({@code false}).
         */
        public synchronized boolean isConverted() {
            return this.converted;
        }

        /**
         * Set the converted value of the constructor argument,
         * after processed type conversion.
         */
        public synchronized void setConvertedValue(Object value) {
            this.converted = (value != null);
            this.convertedValue = value;
        }

        /**
         * Return the converted value of the constructor argument,
         * after processed type conversion.
         */
        public synchronized Object getConvertedValue() {
            return this.convertedValue;
        }

        /**
         * Determine whether the content of this ValueHolder is equal
         * to the content of the given other ValueHolder.
         * <p>Note that ValueHolder does not implement {@code equals}
         * directly, to allow for multiple ValueHolder instances with the
         * same content to reside in the same Set.
         */
        private boolean contentEquals(ValueHolder other) {
            return (this == other ||
                    (ObjectUtils.nullSafeEquals(this.value, other.value) && ObjectUtils.nullSafeEquals(this.type, other.type)));
        }

        /**
         * Determine whether the hash code of the content of this ValueHolder.
         * <p>Note that ValueHolder does not implement {@code hashCode}
         * directly, to allow for multiple ValueHolder instances with the
         * same content to reside in the same Set.
         */
        private int contentHashCode() {
            return ObjectUtils.nullSafeHashCode(this.value) * 29 + ObjectUtils.nullSafeHashCode(this.type);
        }

        /**
         * Create a copy of this ValueHolder: that is, an independent
         * ValueHolder instance with the same contents.
         */
        public ValueHolder copy() {
            ValueHolder copy = new ValueHolder(this.value, this.type, this.name);
            copy.setSource(this.source);
            return copy;
        }
    }
}
