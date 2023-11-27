package com.springframework.beans.factory.support;

import com.sun.istack.internal.Nullable;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class MethodOverrides {
    private final Set<MethodOverride> overrides = new CopyOnWriteArraySet<>();
    @Nullable
    public MethodOverride getOverride(Method method) {
        MethodOverride match = null;
        for (MethodOverride candidate : this.overrides) {
            if (candidate.matches(method)) {
                match = candidate;
            }
        }
        return match;
    }
    /**
     * Deep copy constructor.
     */
    public MethodOverrides(MethodOverrides other) {
        addOverrides(other);
    }

    public MethodOverrides() {

    }
    public void addOverride(MethodOverride override) {
        this.overrides.add(override);
    }
    /**
     * Copy all given method overrides into this object.
     */
    public void addOverrides(MethodOverrides other) {
        if (other != null) {
            this.overrides.addAll(other.overrides);
        }
    }

    /**
     * Return whether the set of method overrides is empty.
     */
    public boolean isEmpty() {
        return this.overrides.isEmpty();
    }
}
