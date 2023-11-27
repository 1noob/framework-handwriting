package com.springframework.core.type.filter;

import com.springframework.core.type.classreading.MetadataReader;
import com.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
@FunctionalInterface
public interface TypeFilter {

    /**
     * Determine whether this filter matches for the class described by
     * the given metadata.
     * @param metadataReader the metadata reader for the target class
     * @param metadataReaderFactory a factory for obtaining metadata readers
     * for other classes (such as superclasses and interfaces)
     * @return whether this filter matches
     * @throws IOException in case of I/O failure when reading metadata
     */
    boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
            throws IOException;

}

