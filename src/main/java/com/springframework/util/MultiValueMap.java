package com.springframework.util;

import com.sun.istack.internal.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface MultiValueMap<K, V> extends Map<K, List<V>> {
    void add(K key, @Nullable V value);
    void addAll(MultiValueMap<K, V> values);

    void add(Object o);
}
