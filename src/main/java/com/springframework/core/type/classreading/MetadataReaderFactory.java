package com.springframework.core.type.classreading;

import com.springframework.core.io.Resource;

import java.io.IOException;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface MetadataReaderFactory {
    MetadataReader getMetadataReader(Resource resource) throws IOException;
    MetadataReader getMetadataReader(String className) throws IOException;

}
