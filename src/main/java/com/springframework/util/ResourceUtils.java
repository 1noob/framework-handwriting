package com.springframework.util;

import com.springframework.core.io.support.ResourcePatternResolver;
import com.sun.istack.internal.Nullable;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class ResourceUtils {
    /** Pseudo URL prefix for loading from the class path: "classpath:". */
    public static final String CLASSPATH_URL_PREFIX = "classpath:";
    /**
     * URL protocol for an entry from a JBoss jar file: "vfszip".
     */
    public static final String URL_PROTOCOL_VFSZIP = "vfszip";
    /**
     * URL protocol for a general JBoss VFS resource: "vfs".
     */
    public static final String URL_PROTOCOL_VFS = "vfs";

    /** URL prefix for loading from a jar file: "jar:". */
    public static final String JAR_URL_PREFIX = "jar:";
    /**
     * URL protocol for an entry from a war file: "war".
     */
    public static final String URL_PROTOCOL_WAR = "war";

    /**
     * URL protocol for an entry from a zip file: "zip".
     */
    public static final String URL_PROTOCOL_ZIP = "zip";

    /**
     * URL protocol for an entry from a WebSphere jar file: "wsjar".
     */
    public static final String URL_PROTOCOL_WSJAR = "wsjar";
    /**
     * URL protocol for an entry from a jar file: "jar".
     */
    public static final String URL_PROTOCOL_JAR = "jar";

    /** Separator between JAR URL and file path within the JAR: "!/". */
    public static final String JAR_URL_SEPARATOR = "!/";

    /** Special separator between WAR URL and jar part on Tomcat. */
    public static final String WAR_URL_SEPARATOR = "*/";

    /** URL prefix for loading from the file system: "file:". */
    public static final String FILE_URL_PREFIX = "file:";

    /** URL protocol for a file in the file system: "file". */
    public static final String URL_PROTOCOL_FILE = "file";

    /** URL protocol for a JBoss file system resource: "vfsfile". */
    public static final String URL_PROTOCOL_VFSFILE = "vfsfile";

    public static boolean isFileURL(URL url) {
        String protocol = url.getProtocol();
        return (URL_PROTOCOL_FILE.equals(protocol) || URL_PROTOCOL_VFSFILE.equals(protocol) ||
                URL_PROTOCOL_VFS.equals(protocol));
    }
    public static boolean isUrl(@Nullable String resourceLocation) {
        return (resourceLocation != null &&
                (resourceLocation.startsWith(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX) ||
                        ResourceUtils.isUrl(resourceLocation)));
    }
    public static boolean isJarURL(URL url) {
        String protocol = url.getProtocol();
        return (URL_PROTOCOL_JAR.equals(protocol) || URL_PROTOCOL_WAR.equals(protocol) ||
                URL_PROTOCOL_ZIP.equals(protocol) || URL_PROTOCOL_VFSZIP.equals(protocol) ||
                URL_PROTOCOL_WSJAR.equals(protocol));
    }

    public static URI toURI(String location) throws URISyntaxException {
        return new URI(StringUtils.replace(location, " ", "%20"));
    }

    public static void useCachesIfNecessary(URLConnection con) {
        con.setUseCaches(con.getClass().getSimpleName().startsWith("JNLP"));
    }
}
