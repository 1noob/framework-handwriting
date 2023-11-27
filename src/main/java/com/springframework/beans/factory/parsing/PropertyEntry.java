package com.springframework.beans.factory.parsing;

import com.springframework.util.StringUtils;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class PropertyEntry implements ParseState.Entry {

    private final String name;


    /**
     * Creates a new instance of the {@link PropertyEntry} class.
     * @param name the name of the JavaBean property represented by this instance
     * @throws IllegalArgumentException if the supplied {@code name} is {@code null}
     * or consists wholly of whitespace
     */
    public PropertyEntry(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("Invalid property name '" + name + "'.");
        }
        this.name = name;
    }


    @Override
    public String toString() {
        return "Property '" + this.name + "'";
    }

}
