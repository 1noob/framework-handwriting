package com.springframework.context;

import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface HierarchicalMessageSource extends MessageSource {
    /**
     * Set the parent that will be used to try to resolve messages
     * that this object can't resolve.
     * @param parent the parent MessageSource that will be used to
     * resolve messages that this object can't resolve.
     * May be {@code null}, in which case no further resolution is possible.
     */
    void setParentMessageSource(@Nullable MessageSource parent);

    /**
     * Return the parent of this MessageSource, or {@code null} if none.
     */
    @Nullable
    MessageSource getParentMessageSource();
}
