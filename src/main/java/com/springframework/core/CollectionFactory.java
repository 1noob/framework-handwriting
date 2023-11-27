package com.springframework.core;

import com.springframework.util.Assert;
import com.springframework.util.LinkedMultiValueMap;
import com.springframework.util.MultiValueMap;
import com.springframework.util.ReflectionUtils;
import com.sun.istack.internal.Nullable;

import java.util.*;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class CollectionFactory {
    public static <K, V> Map<K, V> createMap(Class<?> mapType, @Nullable Class<?> keyType, int capacity) {
        Assert.notNull(mapType, "Map type must not be null");
        if (mapType.isInterface()) {
            if (Map.class == mapType) {
                return new LinkedHashMap<>(capacity);
            }
            else if (SortedMap.class == mapType || NavigableMap.class == mapType) {
                return new TreeMap<>();
            }
            else if (MultiValueMap.class == mapType) {
                return new LinkedMultiValueMap();
            }
            else {
                throw new IllegalArgumentException("Unsupported Map interface: " + mapType.getName());
            }
        }
        else if (EnumMap.class == mapType) {
            Assert.notNull(keyType, "Cannot create EnumMap for unknown key type");
            return new EnumMap(asEnumType(keyType));
        }
        else {
            if (!Map.class.isAssignableFrom(mapType)) {
                throw new IllegalArgumentException("Unsupported Map type: " + mapType.getName());
            }
            try {
                return (Map<K, V>) ReflectionUtils.accessibleConstructor(mapType).newInstance();
            }
            catch (Throwable ex) {
                throw new IllegalArgumentException("Could not instantiate Map type: " + mapType.getName(), ex);
            }
        }
    }
    public static <E> Collection<E> createCollection(Class<?> collectionType, @Nullable Class<?> elementType, int capacity) {
        Assert.notNull(collectionType, "Collection type must not be null");
        if (collectionType.isInterface()) {
            if (Set.class == collectionType || Collection.class == collectionType) {
                return new LinkedHashSet<>(capacity);
            }
            else if (List.class == collectionType) {
                return new ArrayList<>(capacity);
            }
            else if (SortedSet.class == collectionType || NavigableSet.class == collectionType) {
                return new TreeSet<>();
            }
            else {
                throw new IllegalArgumentException("Unsupported Collection interface: " + collectionType.getName());
            }
        }
        else if (EnumSet.class.isAssignableFrom(collectionType)) {
            Assert.notNull(elementType, "Cannot create EnumSet for unknown element type");
            // Cast is necessary for compilation in Eclipse 4.4.1.
            return (Collection<E>) EnumSet.noneOf(asEnumType(elementType));
        }
        else {
            if (!Collection.class.isAssignableFrom(collectionType)) {
                throw new IllegalArgumentException("Unsupported Collection type: " + collectionType.getName());
            }
            try {
                return (Collection<E>) ReflectionUtils.accessibleConstructor(collectionType).newInstance();
            }
            catch (Throwable ex) {
                throw new IllegalArgumentException(
                        "Could not instantiate Collection type: " + collectionType.getName(), ex);
            }
        }
    }
    private static Class<? extends Enum> asEnumType(Class<?> enumType) {
        Assert.notNull(enumType, "Enum type must not be null");
        if (!Enum.class.isAssignableFrom(enumType)) {
            throw new IllegalArgumentException("Supplied type is not an enum: " + enumType.getName());
        }
        return enumType.asSubclass(Enum.class);
    }
}
