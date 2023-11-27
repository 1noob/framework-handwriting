package com.springframework.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class DescriptiveResource  extends AbstractResource {
    private final String description;

    public DescriptiveResource(String description) {
        this.description = (description != null ? description : "");
    }

    @Override
    public URL getURL() throws IOException {
        return null;
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getFilename() {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }
}
