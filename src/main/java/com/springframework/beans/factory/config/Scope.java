package com.springframework.beans.factory.config;

import com.springframework.beans.factory.ObjectFactory;
import com.sun.istack.internal.Nullable;

public interface Scope {
    Object get(String name, ObjectFactory<?> objectFactory);
    Object remove(String name);
}
