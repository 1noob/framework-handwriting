package com.springframework.beans.factory.parsing;

import com.sun.istack.internal.Nullable;

import java.util.LinkedList;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public final class ParseState {

    /**
     * Tab character used when rendering the tree-style representation.
     */
    private static final char TAB = '\t';

    /**
     * Internal {@link LinkedList} storage.
     */
    private final LinkedList<Entry> state;


    /**
     * Create a new {@code ParseState} with an empty {@link LinkedList}.
     */
    public ParseState() {
        this.state = new LinkedList<>();
    }

    /**
     * Create a new {@code ParseState} whose {@link LinkedList} is a {@link Object#clone clone}
     * of that of the passed in {@code ParseState}.
     */
    @SuppressWarnings("unchecked")
    private ParseState(ParseState other) {
        this.state = (LinkedList<Entry>) other.state.clone();
    }


    /**
     * Add a new {@link Entry} to the {@link LinkedList}.
     */
    public void push(Entry entry) {
        this.state.push(entry);
    }

    /**
     * Remove an {@link Entry} from the {@link LinkedList}.
     */
    public void pop() {
        this.state.pop();
    }

    /**
     * Return the {@link Entry} currently at the top of the {@link LinkedList} or
     * {@code null} if the {@link LinkedList} is empty.
     */
    @Nullable
    public Entry peek() {
        return this.state.peek();
    }

    /**
     * Create a new instance of {@link ParseState} which is an independent snapshot
     * of this instance.
     */
    public ParseState snapshot() {
        return new ParseState(this);
    }


    /**
     * Returns a tree-style representation of the current {@code ParseState}.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < this.state.size(); x++) {
            if (x > 0) {
                sb.append('\n');
                for (int y = 0; y < x; y++) {
                    sb.append(TAB);
                }
                sb.append("-> ");
            }
            sb.append(this.state.get(x));
        }
        return sb.toString();
    }


    /**
     * Marker interface for entries into the {@link ParseState}.
     */
    public interface Entry {

    }

}
