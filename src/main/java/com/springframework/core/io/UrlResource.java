package com.springframework.core.io;

import com.springframework.util.Assert;
import com.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class UrlResource extends AbstractFileResolvingResource {
    /**
     * Original URL, used for actual access.
     */
    private final URL url;

    private final URI uri;
    /**
     * Cleaned URL (with normalized path), used for comparisons.
     */
    private final URL cleanedUrl;

    @Override
    public URL getURL() {
        return this.url;
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

    public UrlResource(String path) throws MalformedURLException {
        Assert.notNull(path, "Path must not be null");
        this.uri = null;
        this.url = new URL(path);
        this.cleanedUrl = getCleanedUrl(this.url, path);
    }
    public UrlResource(URL url) {
        Assert.notNull(url, "URL must not be null");
        this.url = url;
        this.cleanedUrl = getCleanedUrl(this.url, url.toString());
        this.uri = null;
    }

    private URL getCleanedUrl(URL originalUrl, String originalPath) {
        String cleanedPath = StringUtils.cleanPath(originalPath);
        if (!cleanedPath.equals(originalPath)) {
            try {
                return new URL(cleanedPath);
            } catch (MalformedURLException ex) {
                // Cleaned URL path cannot be converted to URL -> take original URL.
            }
        }
        return originalUrl;
    }

    public UrlResource(String protocol, String location) throws MalformedURLException {
        this(protocol, location, null);
    }

    public UrlResource(String protocol, String location, String fragment) throws MalformedURLException {
        try {
            this.uri = new URI(protocol, location, fragment);
            this.url = this.uri.toURL();
            this.cleanedUrl = getCleanedUrl(this.url, this.uri.toString());
        } catch (URISyntaxException ex) {
            MalformedURLException exToThrow = new MalformedURLException(ex.getMessage());
            exToThrow.initCause(ex);
            throw exToThrow;
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }
}
