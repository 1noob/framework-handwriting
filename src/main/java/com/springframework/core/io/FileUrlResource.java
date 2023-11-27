package com.springframework.core.io;

import com.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class FileUrlResource extends UrlResource implements WritableResource {
    private volatile File file;

    public FileUrlResource(URL url) {
        super(url);
    }

    public FileUrlResource(String location) throws MalformedURLException {
        super(ResourceUtils.URL_PROTOCOL_FILE, location);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }
}
