package com.springframework.beans.factory;

import com.sun.istack.internal.Nullable;

/**
 * @author Gary
 */
public interface HierarchicalBeanFactory extends BeanFactory{

    /**
     * Return the parent bean factory, or {@code null} if there is none.
     */
    @Nullable
    BeanFactory getParentBeanFactory();

    boolean containsLocalBean(String name);
}
