package com.springframework.beans.factory.config;

import com.sun.istack.internal.Nullable;

import java.io.Serializable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public final class AutowiredPropertyMarker implements Serializable {

    /**
     * The canonical instance for the autowired marker value.
     */
    public static final Object INSTANCE = new AutowiredPropertyMarker();


    private AutowiredPropertyMarker() {
    }

    private Object readResolve() {
        return INSTANCE;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        return (this == obj);
    }

    @Override
    public int hashCode() {
        return AutowiredPropertyMarker.class.hashCode();
    }

    @Override
    public String toString() {
        return "(autowired)";
    }

}
