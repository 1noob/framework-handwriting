package com.springframework.core.io;

import com.springframework.util.Assert;
import com.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Path;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class FileSystemResource extends AbstractResource implements WritableResource {

    private final String path;

    private final File file;

    private final Path filePath;

    public FileSystemResource(String path) {
        Assert.notNull(path, "Path must not be null");
        this.path = StringUtils.cleanPath(path);
        this.file = new File(path);
        this.filePath = this.file.toPath();
    }

    public FileSystemResource(File file) {
        Assert.notNull(file, "File must not be null");
        this.path = StringUtils.cleanPath(file.getPath());
        this.file = file;
        this.filePath = file.toPath();
    }

    public FileSystemResource(Path filePath) {
        Assert.notNull(filePath, "Path must not be null");
        this.path = StringUtils.cleanPath(filePath.toString());
        this.file = null;
        this.filePath = filePath;
    }

    public FileSystemResource(FileSystem fileSystem, String path) {
        Assert.notNull(fileSystem, "FileSystem must not be null");
        Assert.notNull(path, "Path must not be null");
        this.path = StringUtils.cleanPath(path);
        this.file = null;
        this.filePath = fileSystem.getPath(this.path).normalize();
    }

    public final String getPath() {
        return this.path;
    }

    @Override
    public URL getURL() throws IOException {
        return (this.file != null ? this.file.toURI().toURL() : this.filePath.toUri().toURL());
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
