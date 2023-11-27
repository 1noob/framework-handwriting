package com.springframework.util;

import java.io.Serializable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class LinkedMultiValueMap<K, V> extends MultiValueMapAdapter<K, V> implements Serializable, Cloneable {
    @Override
    public void addAll(MultiValueMap<K, V> values) {

    }
}
