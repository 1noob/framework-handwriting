package com.springframework.core.io;

import com.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class VfsResource extends AbstractResource {
    private final Object resource;

    public VfsResource(Object resource) {
        Assert.notNull(resource, "VirtualFile must not be null");
        this.resource = resource;
    }

    @Override
    public URL getURL() throws IOException {
        try {
            return VfsUtils.getURL(this.resource);
        }
        catch (Exception ex) {
            throw new IOException("Failed to obtain URL for file " + this.resource, ex);
        }
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
