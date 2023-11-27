package com.springframework.beans.factory;

public interface ObjectFactory<T> {
    T getObject() throws RuntimeException;
}
