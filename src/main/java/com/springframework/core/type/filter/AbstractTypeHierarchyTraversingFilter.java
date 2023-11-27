package com.springframework.core.type.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class AbstractTypeHierarchyTraversingFilter implements TypeFilter {

    protected final Log logger = LogFactory.getLog(getClass());

    private final boolean considerInherited;

    private final boolean considerInterfaces;


    protected AbstractTypeHierarchyTraversingFilter(boolean considerInherited, boolean considerInterfaces) {
        this.considerInherited = considerInherited;
        this.considerInterfaces = considerInterfaces;
    }
}
