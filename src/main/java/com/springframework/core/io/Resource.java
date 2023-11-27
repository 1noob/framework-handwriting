package com.springframework.core.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface Resource extends InputStreamSource {
    URL getURL() throws IOException;
    Resource createRelative(String relativePath) throws IOException;
    File getFile() throws IOException;
    String getDescription();
    String getFilename();
    boolean exists();
    default boolean isFile() {
        return false;
    }
    default boolean isOpen() {
        return false;
    }
    default boolean isReadable() {
        return exists();
    }
}
