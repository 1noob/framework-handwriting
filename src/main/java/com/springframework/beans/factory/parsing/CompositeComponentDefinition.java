package com.springframework.beans.factory.parsing;

import com.springframework.util.Assert;
import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class CompositeComponentDefinition extends AbstractComponentDefinition {
    private final String name;

    @Nullable
    private final Object source;

    private final List<ComponentDefinition> nestedComponents = new ArrayList<>();

    public CompositeComponentDefinition(String name, @Nullable Object source) {
        Assert.notNull(name, "Name must not be null");
        this.name = name;
        this.source = source;
    }


    public void addNestedComponent(ComponentDefinition component) {
        Assert.notNull(component, "ComponentDefinition must not be null");
        this.nestedComponents.add(component);
    }

}
