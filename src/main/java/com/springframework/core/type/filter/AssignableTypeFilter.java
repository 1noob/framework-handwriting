package com.springframework.core.type.filter;

import com.springframework.core.type.classreading.MetadataReaderFactory;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;

import java.io.IOException;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class AssignableTypeFilter extends AbstractTypeHierarchyTraversingFilter {
    private final Class<?> targetType;

    protected AssignableTypeFilter(boolean considerInherited, boolean considerInterfaces, Class<?> targetType) {
        super(considerInherited, considerInterfaces);
        this.targetType = targetType;
    }

    public final Class<?> getTargetType() {
        return this.targetType;
    }


    @Override
    public boolean match(com.springframework.core.type.classreading.MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        return false;
    }
}
