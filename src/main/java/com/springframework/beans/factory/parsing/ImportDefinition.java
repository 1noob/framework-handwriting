package com.springframework.beans.factory.parsing;

import com.springframework.beans.BeanMetadataElement;
import com.springframework.core.io.Resource;
import com.springframework.util.Assert;
import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class ImportDefinition implements BeanMetadataElement {

    private final String importedResource;

    @Nullable
    private final Resource[] actualResources;

    @Nullable
    private final Object source;


    /**
     * Create a new ImportDefinition.
     * @param importedResource the location of the imported resource
     */
    public ImportDefinition(String importedResource) {
        this(importedResource, null, null);
    }

    /**
     * Create a new ImportDefinition.
     * @param importedResource the location of the imported resource
     * @param source the source object (may be {@code null})
     */
    public ImportDefinition(String importedResource, @Nullable Object source) {
        this(importedResource, null, source);
    }

    /**
     * Create a new ImportDefinition.
     * @param importedResource the location of the imported resource
     * @param source the source object (may be {@code null})
     */
    public ImportDefinition(String importedResource, @Nullable Resource[] actualResources, @Nullable Object source) {
        Assert.notNull(importedResource, "Imported resource must not be null");
        this.importedResource = importedResource;
        this.actualResources = actualResources;
        this.source = source;
    }


    /**
     * Return the location of the imported resource.
     */
    public final String getImportedResource() {
        return this.importedResource;
    }

    @Nullable
    public final Resource[] getActualResources() {
        return this.actualResources;
    }

    @Override
    @Nullable
    public final Object getSource() {
        return this.source;
    }

}

