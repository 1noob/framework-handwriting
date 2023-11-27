package com.springframework.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
class MultiValueMapAdapter<K, V> implements MultiValueMap<K, V>, Serializable {
    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public List<V> get(Object key) {
        return null;
    }

    @Override
    public List<V> put(K key, List<V> value) {
        return null;
    }

    @Override
    public List<V> remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends List<V>> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<List<V>> values() {
        return null;
    }

    @Override
    public Set<Entry<K, List<V>>> entrySet() {
        return null;
    }

    @Override
    public void add(K key, V value) {

    }

    @Override
    public void addAll(MultiValueMap<K, V> values) {

    }

    @Override
    public void add(Object o) {

    }
}
