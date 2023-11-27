package com.springframework.beans;


import com.springframework.core.CollectionFactory;
import com.springframework.core.ResolvableType;
import com.springframework.core.convert.TypeDescriptor;
import com.springframework.util.Assert;
import com.springframework.util.ObjectUtils;
import com.springframework.util.StringUtils;
import com.sun.istack.internal.Nullable;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.security.PrivilegedActionException;
import java.util.*;

/**
 * @Author 虎哥
 * @Description //TODO
 * 要带着问题去学习,多猜想多验证
 **/
public abstract class AbstractNestablePropertyAccessor extends AbstractPropertyAccessor {

    @Nullable
    Object wrappedObject;

    private String nestedPath = "";

    @Nullable
    Object rootObject;
    @Nullable
    private Map<String, AbstractNestablePropertyAccessor> nestedPropertyAccessors;

    public void setWrappedInstance(Object object, @Nullable String nestedPath, @Nullable Object rootObject) {
        this.wrappedObject = ObjectUtils.unwrapOptional(object);
        Assert.notNull(this.wrappedObject, "Target object must not be null");
        this.nestedPath = (nestedPath != null ? nestedPath : "");
        this.rootObject = (!this.nestedPath.isEmpty() ? rootObject : this.wrappedObject);
        this.nestedPropertyAccessors = null;
//        this.typeConverterDelegate = new TypeConverterDelegate(this, this.wrappedObject);
    }

    public final Object getWrappedInstance() {
        Assert.state(this.wrappedObject != null, "No wrapped object");
        return this.wrappedObject;
    }
    protected AbstractNestablePropertyAccessor(Object object) {
        this.nestedPath = "";
        this.registerDefaultEditors();
        this.setWrappedInstance(object);
    }
    public void setWrappedInstance(Object object) {
        this.setWrappedInstance(object, "", (Object)null);
    }

    public final Class<?> getWrappedClass() {
        return getWrappedInstance().getClass();
    }
    protected AbstractNestablePropertyAccessor getPropertyAccessorForPropertyPath(String propertyPath) throws Exception {
        int pos = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(propertyPath);
        if (pos > -1) {
            String nestedProperty = propertyPath.substring(0, pos);
            String nestedPath = propertyPath.substring(pos + 1);
            AbstractNestablePropertyAccessor nestedPa = this.getNestedPropertyAccessor(nestedProperty);
            return nestedPa.getPropertyAccessorForPropertyPath(nestedPath);
        } else {
            return this;
        }
    }
    private PropertyValue createDefaultPropertyValue(AbstractNestablePropertyAccessor.PropertyTokenHolder tokens) {
        TypeDescriptor desc = this.getPropertyTypeDescriptor(tokens.canonicalName);
        Object defaultValue = this.newValue(desc.getType(), desc, tokens.canonicalName);
        return new PropertyValue(tokens.canonicalName, defaultValue);
    }
    private Object newValue(Class<?> type, @Nullable TypeDescriptor desc, String name) {
        try {
            if (type.isArray()) {
                Class<?> componentType = type.getComponentType();
                if (componentType.isArray()) {
                    Object array = Array.newInstance(componentType, 1);
                    Array.set(array, 0, Array.newInstance(componentType.getComponentType(), 0));
                    return array;
                } else {
                    return Array.newInstance(componentType, 0);
                }
            } else {
                TypeDescriptor keyDesc;
                if (Collection.class.isAssignableFrom(type)) {
                    keyDesc = desc != null ? desc.getElementTypeDescriptor() : null;
                    return CollectionFactory.createCollection(type, keyDesc != null ? keyDesc.getType() : null, 16);
                } else if (Map.class.isAssignableFrom(type)) {
                    keyDesc = desc != null ? desc.getMapKeyTypeDescriptor() : null;
                    return CollectionFactory.createMap(type, keyDesc != null ? keyDesc.getType() : null, 16);
                } else {
                    Constructor<?> ctor = type.getDeclaredConstructor();
                    if (Modifier.isPrivate(ctor.getModifiers())) {
                        throw new IllegalAccessException("Auto-growing not allowed with private constructor: " + ctor);
                    } else {
                        return BeanUtils.instantiateClass(ctor, new Object[0]);
                    }
                }
            }
        } catch (Throwable var6) {
            throw new RuntimeException("Could not instantiate property type [" + type.getName() + "] to auto-grow nested property path", var6);
        }
    }
    private AbstractNestablePropertyAccessor.PropertyTokenHolder getPropertyNameTokens(String propertyName) {
        String actualName = null;
        List<String> keys = new ArrayList(2);
        int searchIndex = 0;

        while(true) {
            int keyStart;
            int keyEnd;
            do {
                do {
                    if (searchIndex == -1) {
                        AbstractNestablePropertyAccessor.PropertyTokenHolder tokens = new AbstractNestablePropertyAccessor.PropertyTokenHolder(actualName != null ? actualName : propertyName);
                        if (!keys.isEmpty()) {
                            tokens.canonicalName = tokens.canonicalName + "[" + StringUtils.collectionToDelimitedString(keys, "][") + "]";
                            tokens.keys = StringUtils.toStringArray(keys);
                        }

                        return tokens;
                    }

                    keyStart = propertyName.indexOf("[", searchIndex);
                    searchIndex = -1;
                } while(keyStart == -1);

                keyEnd = this.getPropertyNameKeyEnd(propertyName, keyStart + "[".length());
            } while(keyEnd == -1);

            if (actualName == null) {
                actualName = propertyName.substring(0, keyStart);
            }

            String key = propertyName.substring(keyStart + "[".length(), keyEnd);
            if (key.length() > 1 && key.startsWith("'") && key.endsWith("'") || key.startsWith("\"") && key.endsWith("\"")) {
                key = key.substring(1, key.length() - 1);
            }

            keys.add(key);
            searchIndex = keyEnd + "]".length();
        }
    }
    private int getPropertyNameKeyEnd(String propertyName, int startIndex) {
        int unclosedPrefixes = 0;
        int length = propertyName.length();

        for(int i = startIndex; i < length; ++i) {
            switch(propertyName.charAt(i)) {
                case '[':
                    ++unclosedPrefixes;
                    break;
                case ']':
                    if (unclosedPrefixes == 0) {
                        return i;
                    }

                    --unclosedPrefixes;
            }
        }

        return -1;
    }
    protected abstract static class PropertyHandler {
        private final Class<?> propertyType;
        private final boolean readable;
        private final boolean writable;

        public PropertyHandler(Class<?> propertyType, boolean readable, boolean writable) {
            this.propertyType = propertyType;
            this.readable = readable;
            this.writable = writable;
        }

        public Class<?> getPropertyType() {
            return this.propertyType;
        }

        public boolean isReadable() {
            return this.readable;
        }

        public boolean isWritable() {
            return this.writable;
        }

        public abstract TypeDescriptor toTypeDescriptor();

        public abstract ResolvableType getResolvableType();

        @Nullable
        public Class<?> getMapKeyType(int nestingLevel) {
            return this.getResolvableType().getNested(nestingLevel).asMap().resolveGeneric(new int[]{0});
        }

        @Nullable
        public Class<?> getMapValueType(int nestingLevel) {
            return this.getResolvableType().getNested(nestingLevel).asMap().resolveGeneric(new int[]{1});
        }

        @Nullable
        public Class<?> getCollectionType(int nestingLevel) {
            return this.getResolvableType().getNested(nestingLevel).asCollection().resolveGeneric(new int[0]);
        }

        @Nullable
        public abstract TypeDescriptor nested(int var1);

        @Nullable
        public abstract Object getValue() throws Exception;

        public abstract void setValue(@Nullable Object var1) throws Exception;
    }
    protected abstract AbstractNestablePropertyAccessor.PropertyHandler getLocalPropertyHandler(String var1) throws CloneNotSupportedException;

    @Nullable
    protected Object getPropertyValue(AbstractNestablePropertyAccessor.PropertyTokenHolder tokens) throws Exception {
        String propertyName = tokens.canonicalName;
        String actualName = tokens.actualName;
        AbstractNestablePropertyAccessor.PropertyHandler ph = this.getLocalPropertyHandler(actualName);
        if (ph != null && ph.isReadable()) {
            try {
                Object value = ph.getValue();
                if (tokens.keys != null) {
                    if (value == null) {
                        if (!this.isAutoGrowNestedPaths()) {
                        }

                        value = this.setDefaultValue(new AbstractNestablePropertyAccessor.PropertyTokenHolder(tokens.actualName));
                    }

                    StringBuilder indexedPropertyName = new StringBuilder(tokens.actualName);

                    for(int i = 0; i < tokens.keys.length; ++i) {
                        String key = tokens.keys[i];
                        if (value == null) {
                        }

                        int index;
                        if (value.getClass().isArray()) {
                            index = Integer.parseInt(key);
                            value = this.growArrayIfNecessary(value, index, indexedPropertyName.toString());
                            value = Array.get(value, index);
                        } else if (value instanceof List) {
                            index = Integer.parseInt(key);
                            List<Object> list = (List)value;
                            this.growCollectionIfNecessary(list, index, indexedPropertyName.toString(), ph, i + 1);
                            value = list.get(index);
                        } else if (value instanceof Set) {
                            Set<Object> set = (Set)value;
                            index = Integer.parseInt(key);
                            if (index < 0 || index >= set.size()) {
                            }

                            Iterator<Object> it = set.iterator();

                            for(int j = 0; it.hasNext(); ++j) {
                                Object elem = it.next();
                                if (j == index) {
                                    value = elem;
                                    break;
                                }
                            }
                        } else {
                            if (!(value instanceof Map)) {
                            }

                            Map<Object, Object> map = (Map)value;
                            Class<?> mapKeyType = ph.getResolvableType().getNested(i + 1).asMap().resolveGeneric(new int[]{0});
                            TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(mapKeyType);
//                            Object convertedMapKey = this.convertIfNecessary((String)null, (Object)null, key, mapKeyType, typeDescriptor);
                            value = map.get(value);
                        }

                        indexedPropertyName.append("[").append(key).append("]");
                    }
                }

                return value;
            } catch (Exception var14) {
                throw new RuntimeException("Itllegal attempt to get property '" + actualName + "' threw exception");
            }
        } else {
            throw new RuntimeException(this.nestedPath + propertyName);
        }
    }
//    @Nullable
//    TypeConverterDelegate typeConverterDelegate;

    //    @Nullable
//    private Object convertIfNecessary(@Nullable String propertyName, @Nullable Object oldValue, @Nullable Object newValue, @Nullable Class<?> requiredType, @Nullable TypeDescriptor td) throws TypeMismatchException {
//        Assert.state(this.typeConverterDelegate != null, "No TypeConverterDelegate");
//
//        PropertyChangeEvent pce;
//        try {
//            return this.typeConverterDelegate.convertIfNecessary(propertyName, oldValue, newValue, requiredType, td);
//        } catch (IllegalStateException | ConverterNotFoundException var8) {
//            pce = new PropertyChangeEvent(this.getRootInstance(), this.nestedPath + propertyName, oldValue, newValue);
//            throw new ConversionNotSupportedException(pce, requiredType, var8);
//        } catch (IllegalArgumentException | ConversionException var9) {
//            pce = new PropertyChangeEvent(this.getRootInstance(), this.nestedPath + propertyName, oldValue, newValue);
//            throw new TypeMismatchException(pce, requiredType, var9);
//        }
//    }
    private void growCollectionIfNecessary(Collection<Object> collection, int index, String name, AbstractNestablePropertyAccessor.PropertyHandler ph, int nestingLevel) {
        if (this.isAutoGrowNestedPaths()) {
            int size = collection.size();
            if (index >= size && index < this.autoGrowCollectionLimit) {
                Class<?> elementType = ph.getResolvableType().getNested(nestingLevel).asCollection().resolveGeneric(new int[0]);
                if (elementType != null) {
                    for(int i = collection.size(); i < index + 1; ++i) {
                        collection.add(this.newValue(elementType, (TypeDescriptor)null, name));
                    }
                }
            }

        }
    }
    private Object growArrayIfNecessary(Object array, int index, String name) {
        if (!this.isAutoGrowNestedPaths()) {
            return array;
        } else {
            int length = Array.getLength(array);
            if (index >= length && index < this.autoGrowCollectionLimit) {
                Class<?> componentType = array.getClass().getComponentType();
                Object newArray = Array.newInstance(componentType, index + 1);
                System.arraycopy(array, 0, newArray, 0, length);

                for(int i = length; i < Array.getLength(newArray); ++i) {
                    Array.set(newArray, i, this.newValue(componentType, (TypeDescriptor)null, name));
                }

                this.setPropertyValue(name, newArray);
                Object defaultValue = this.getPropertyValue(name);
                Assert.state(defaultValue != null, "Default value must not be null");
                return defaultValue;
            } else {
                return array;
            }
        }
    }
    protected void setPropertyValue(AbstractNestablePropertyAccessor.PropertyTokenHolder tokens, PropertyValue pv) throws  Exception {
        if (tokens.keys != null) {
            this.processKeyedProperty(tokens, pv);
        } else {
            this.processLocalProperty(tokens, pv);
        }

    }
    private void processLocalProperty(AbstractNestablePropertyAccessor.PropertyTokenHolder tokens, PropertyValue pv) throws Exception {
        AbstractNestablePropertyAccessor.PropertyHandler ph = this.getLocalPropertyHandler(tokens.actualName);
        if (ph != null && ph.isWritable()) {
            Object oldValue = null;

            PropertyChangeEvent propertyChangeEvent;
            try {
                Object originalValue = pv.getValue();
                Object valueToApply = originalValue;
                if (!Boolean.FALSE.equals(pv.conversionNecessary)) {
                    if (pv.isConverted()) {
                        valueToApply = pv.getConvertedValue();
                    } else {
                        if (this.isExtractOldValueForEditor() && ph.isReadable()) {
                            try {
                                oldValue = ph.getValue();
                            } catch (Exception var8) {
                                Exception ex = var8;
                                if (var8 instanceof PrivilegedActionException) {
                                    ex = ((PrivilegedActionException)var8).getException();
                                }

//                                if (logger.isDebugEnabled()) {
//                                    logger.debug("Could not read previous value of property '" + this.nestedPath + tokens.canonicalName + "'", ex);
//                                }
                            }
                        }

//                        valueToApply = this.convertForProperty(tokens.canonicalName, oldValue, originalValue, ph.toTypeDescriptor());
                    }

                    pv.getOriginalPropertyValue().conversionNecessary = valueToApply != originalValue;
                }

                ph.setValue(valueToApply);
            } catch (Exception var9) {

                throw new RuntimeException(var9);
            }
        } else if (pv.isOptional()) {
//            if (logger.isDebugEnabled()) {
//                logger.debug("Ignoring optional value for property '" + tokens.actualName + "' - property not found on bean class [" + this.getRootClass().getName() + "]");
//            }

        } else {
            throw this.createNotWritablePropertyException(tokens.canonicalName);
        }
    }

    protected abstract Exception createNotWritablePropertyException(String var1);

    private Object getPropertyHoldingValue(AbstractNestablePropertyAccessor.PropertyTokenHolder tokens) throws Exception {
        Assert.state(tokens.keys != null, "No token keys");
        AbstractNestablePropertyAccessor.PropertyTokenHolder getterTokens = new AbstractNestablePropertyAccessor.PropertyTokenHolder(tokens.actualName);
        getterTokens.canonicalName = tokens.canonicalName;
        getterTokens.keys = new String[tokens.keys.length - 1];
        System.arraycopy(tokens.keys, 0, getterTokens.keys, 0, tokens.keys.length - 1);

        Object propValue;
        propValue = this.getPropertyValue(getterTokens);

        if (propValue == null) {
            if (!this.isAutoGrowNestedPaths()) {
//                throw new NullValueInNestedPathException(this.getRootClass(), this.nestedPath + tokens.canonicalName, "Cannot access indexed value in property referenced in indexed property path '" + tokens.canonicalName + "': returned null");
            }

            int lastKeyIndex = tokens.canonicalName.lastIndexOf(91);
            getterTokens.canonicalName = tokens.canonicalName.substring(0, lastKeyIndex);
            propValue = this.setDefaultValue(getterTokens);
        }

        return propValue;
    }
    private void processKeyedProperty(AbstractNestablePropertyAccessor.PropertyTokenHolder tokens, PropertyValue pv) throws Exception {
        Object propValue = this.getPropertyHoldingValue(tokens);
        AbstractNestablePropertyAccessor.PropertyHandler ph = this.getLocalPropertyHandler(tokens.actualName);
        if (ph == null) {
//            throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + tokens.actualName, "No property handler found");
        } else {
            Assert.state(tokens.keys != null, "No token keys");
            String lastKey = tokens.keys[tokens.keys.length - 1];
            Class requiredType;
            Object convertedValue;
            Object newArray;
            if (propValue.getClass().isArray()) {
                requiredType = propValue.getClass().getComponentType();
                int arrayIndex = Integer.parseInt(lastKey);
                Object oldValue = null;

                try {
                    if (this.isExtractOldValueForEditor() && arrayIndex < Array.getLength(propValue)) {
                        oldValue = Array.get(propValue, arrayIndex);
                    }

//                    convertedValue = this.convertIfNecessary(tokens.canonicalName, oldValue, pv.getValue(), requiredType, ph.nested(tokens.keys.length));
                    int length = Array.getLength(propValue);
                    if (arrayIndex >= length && arrayIndex < this.autoGrowCollectionLimit) {
                        Class<?> componentType = propValue.getClass().getComponentType();
                        newArray = Array.newInstance(componentType, arrayIndex + 1);
                        System.arraycopy(propValue, 0, newArray, 0, length);
                        this.setPropertyValue(tokens.actualName, newArray);
                        propValue = this.getPropertyValue(tokens.actualName);
                    }

                    Array.set(propValue, arrayIndex, oldValue);
                } catch (IndexOutOfBoundsException var16) {
//                    throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + tokens.canonicalName, "Invalid array index in property path '" + tokens.canonicalName + "'", var16);
                }
            } else {
                if (propValue instanceof List) {
                    requiredType = ph.getCollectionType(tokens.keys.length);
                    List<Object> list = (List)propValue;
                    int index = Integer.parseInt(lastKey);
                    convertedValue = null;
                    if (this.isExtractOldValueForEditor() && index < list.size()) {
                        convertedValue = list.get(index);
                    }

//                    convertedValue = this.convertIfNecessary(tokens.canonicalName, convertedValue, pv.getValue(), requiredType, ph.nested(tokens.keys.length));
                    int size = list.size();
                    if (index >= size && index < this.autoGrowCollectionLimit) {
                        for(int i = size; i < index; ++i) {
                            try {
                                list.add((Object)null);
                            } catch (NullPointerException var15) {
//                                throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + tokens.canonicalName, "Cannot set element with index " + index + " in List of size " + size + ", accessed using property path '" + tokens.canonicalName + "': List does not support filling up gaps with null elements");
                            }
                        }

                        list.add(convertedValue);
                    } else {
                        try {
                            list.set(index, convertedValue);
                        } catch (IndexOutOfBoundsException var14) {
//                            throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + tokens.canonicalName, "Invalid list index in property path '" + tokens.canonicalName + "'", var14);
                        }
                    }
                } else {
                    if (!(propValue instanceof Map)) {
//                        throw new InvalidPropertyException(this.getRootClass(), this.nestedPath + tokens.canonicalName, "Property referenced in indexed property path '" + tokens.canonicalName + "' is neither an array nor a List nor a Map; returned value was [" + propValue + "]");
                    }

//                    requiredType = ph.getMapKeyType(tokens.keys.length);
//                    Class<?> mapValueType = ph.getMapValueType(tokens.keys.length);
//                    Map<Object, Object> map = (Map)propValue;
//                    TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(requiredType);
//                    convertedValue = this.convertIfNecessary((String)null, (Object)null, lastKey, requiredType, typeDescriptor);
                    Object oldValue = null;
//                    if (this.isExtractOldValueForEditor()) {
//                        oldValue = map.get(map);
//                    }

//                    newArray = this.convertIfNecessary(tokens.canonicalName, oldValue, pv.getValue(), mapValueType, ph.nested(tokens.keys.length));
//                    map.put(convertedValue, newArray);
                }
            }

        }
    }
    private Object setDefaultValue(AbstractNestablePropertyAccessor.PropertyTokenHolder tokens) throws Exception {
        PropertyValue pv = this.createDefaultPropertyValue(tokens);
        this.setPropertyValue(tokens, pv);
        Object defaultValue = this.getPropertyValue(tokens);
        Assert.state(defaultValue != null, "Default value must not be null");
        return defaultValue;
    }
    private AbstractNestablePropertyAccessor getNestedPropertyAccessor(String nestedProperty) throws Exception {
        if (this.nestedPropertyAccessors == null) {
            this.nestedPropertyAccessors = new HashMap();
        }

        AbstractNestablePropertyAccessor.PropertyTokenHolder tokens = this.getPropertyNameTokens(nestedProperty);
        String canonicalName = tokens.canonicalName;
        Object value = this.getPropertyValue(tokens);
        if (value == null || value instanceof Optional && !((Optional)value).isPresent()) {
            if (!this.isAutoGrowNestedPaths()) {
            }
            value = this.setDefaultValue(tokens);
        }

        AbstractNestablePropertyAccessor nestedPa = (AbstractNestablePropertyAccessor)this.nestedPropertyAccessors.get(canonicalName);
        if (nestedPa != null && nestedPa.getWrappedInstance() == ObjectUtils.unwrapOptional(value)) {

        } else {

            nestedPa = this.newNestedPropertyAccessor(value, this.nestedPath + canonicalName + ".");
            this.copyDefaultEditorsTo(nestedPa);
            this.copyCustomEditorsTo(nestedPa, canonicalName);
            this.nestedPropertyAccessors.put(canonicalName, nestedPa);
        }

        return nestedPa;
    }
    protected abstract AbstractNestablePropertyAccessor newNestedPropertyAccessor(Object var1, String var2);
    protected String getFinalPath(AbstractNestablePropertyAccessor pa, String nestedPath) {
        return pa == this ? nestedPath : nestedPath.substring(PropertyAccessorUtils.getLastNestedPropertySeparatorIndex(nestedPath) + 1);
    }
    @Override
    public void setPropertyValue2(PropertyValue pv) throws Exception {
        AbstractNestablePropertyAccessor.PropertyTokenHolder tokens = (AbstractNestablePropertyAccessor.PropertyTokenHolder)pv.resolvedTokens;
        if (tokens == null) {
            String propertyName = pv.getName();

            AbstractNestablePropertyAccessor nestedPa;
            try {
                nestedPa = this.getPropertyAccessorForPropertyPath(propertyName);
            } catch (Exception var6) {
                throw new RuntimeException( "Nested property in path '" + propertyName + "' does not exist");
            }

            tokens = this.getPropertyNameTokens(this.getFinalPath(nestedPa, propertyName));
            if (nestedPa == this) {
                pv.getOriginalPropertyValue().resolvedTokens = tokens;
            }

            nestedPa.setPropertyValue(tokens, pv);
        } else {
            this.setPropertyValue(tokens, pv);
        }

    }

    protected static class PropertyTokenHolder {
        public String actualName;
        public String canonicalName;
        @Nullable
        public String[] keys;

        public PropertyTokenHolder(String name) {
            this.actualName = name;
            this.canonicalName = name;
        }
    }
    private int autoGrowCollectionLimit;
    public int getAutoGrowCollectionLimit() {
        return this.autoGrowCollectionLimit;
    }
    protected AbstractNestablePropertyAccessor(Object object, String nestedPath, AbstractNestablePropertyAccessor parent) {
        this.autoGrowCollectionLimit = 2147483647;
        this.nestedPath = "";
        this.setWrappedInstance(object, nestedPath, parent.getWrappedInstance());
        this.setExtractOldValueForEditor(parent.isExtractOldValueForEditor());
        this.setAutoGrowNestedPaths(parent.isAutoGrowNestedPaths());
        this.setAutoGrowCollectionLimit(parent.getAutoGrowCollectionLimit());
        this.setConversionService(parent.getConversionService());
    }
    public void setAutoGrowCollectionLimit(int autoGrowCollectionLimit) {
        this.autoGrowCollectionLimit = autoGrowCollectionLimit;
    }
}
