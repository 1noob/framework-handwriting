package com.springframework.core.io.support;

import com.springframework.core.io.InputStreamSource;
import com.springframework.core.io.Resource;
import com.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class EncodedResource implements InputStreamSource {

    private final Resource resource;

    private final String encoding;

    private final Charset charset;

    public EncodedResource(Resource resource) {
        this(resource, null, null);
    }

    private EncodedResource(Resource resource, String encoding,Charset charset) {
        super();
        Assert.notNull(resource, "Resource must not be null");
        this.resource = resource;
        this.encoding = encoding;
        this.charset = charset;
    }

    public final Resource getResource() {
        return this.resource;
    }
    public final String getEncoding() {
        return this.encoding;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.resource.getInputStream();
    }
}
