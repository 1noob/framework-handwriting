package com.springframework.beans.factory.parsing;

import com.springframework.util.Assert;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class ConstructorArgumentEntry implements ParseState.Entry {

    private final int index;


    /**
     * Creates a new instance of the {@link ConstructorArgumentEntry} class
     * representing a constructor argument with a (currently) unknown index.
     */
    public ConstructorArgumentEntry() {
        this.index = -1;
    }

    /**
     * Creates a new instance of the {@link ConstructorArgumentEntry} class
     * representing a constructor argument at the supplied {@code index}.
     * @param index the index of the constructor argument
     * @throws IllegalArgumentException if the supplied {@code index}
     * is less than zero
     */
    public ConstructorArgumentEntry(int index) {
        Assert.isTrue(index >= 0, "Constructor argument index must be greater than or equal to zero");
        this.index = index;
    }


    @Override
    public String toString() {
        return "Constructor-arg" + (this.index >= 0 ? " #" + this.index : "");
    }

}

