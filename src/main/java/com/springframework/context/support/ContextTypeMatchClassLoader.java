package com.springframework.context.support;

import com.springframework.core.DecoratingClassLoader;
import com.springframework.core.SmartClassLoader;
import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
class ContextTypeMatchClassLoader extends DecoratingClassLoader implements SmartClassLoader {
    public ContextTypeMatchClassLoader(@Nullable ClassLoader parent) {
        super(parent);
    }

    @Override
    public boolean isClassReloadable(Class<?> clazz) {
        return false;
    }
}
