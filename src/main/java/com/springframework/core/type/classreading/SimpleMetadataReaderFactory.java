package com.springframework.core.type.classreading;

import com.springframework.core.io.Resource;

import java.io.IOException;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class SimpleMetadataReaderFactory implements MetadataReaderFactory {
    @Override
    public MetadataReader getMetadataReader(Resource resource) throws IOException {
        return null;
    }

    @Override
    public MetadataReader getMetadataReader(String className) throws IOException {
        return null;
    }
}
