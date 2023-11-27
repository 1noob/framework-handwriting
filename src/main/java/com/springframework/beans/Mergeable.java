package com.springframework.beans;

/**
 * @author Gary
 */
public interface Mergeable {
    boolean isMergeEnabled();
    Object merge(Object parent);
}
