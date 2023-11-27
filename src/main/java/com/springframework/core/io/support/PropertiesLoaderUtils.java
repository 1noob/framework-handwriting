package com.springframework.core.io.support;

import com.springframework.core.io.Resource;
import com.springframework.util.Assert;
import com.springframework.util.ClassUtils;
import com.springframework.util.ResourceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class PropertiesLoaderUtils {
    private static final String XML_FILE_EXTENSION = ".xml";
    public static Properties loadProperties(Resource resource) throws IOException {
        Properties props = new Properties();
        fillProperties(props, resource);
        return props;
    }
    public static void fillProperties(Properties props, Resource resource) throws IOException {
        try (InputStream is = resource.getInputStream()) {
            String filename = resource.getFilename();
            if (filename != null && filename.endsWith(XML_FILE_EXTENSION)) {
                props.loadFromXML(is);
            }
            else {
                props.load(is);
            }
        }
    }
    public static Properties loadAllProperties(String resourceName, ClassLoader classLoader) throws IOException {
        Assert.notNull(resourceName, "Resource name must not be null");
        ClassLoader classLoaderToUse = classLoader;
        if (classLoaderToUse == null) {
            classLoaderToUse = ClassUtils.getDefaultClassLoader();
        }
        Enumeration<URL> urls = (classLoaderToUse != null ? classLoaderToUse.getResources(resourceName) :
                ClassLoader.getSystemResources(resourceName));
        Properties props = new Properties();
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            URLConnection con = url.openConnection();
            ResourceUtils.useCachesIfNecessary(con);
            try (InputStream is = con.getInputStream()) {
                if (resourceName.endsWith(XML_FILE_EXTENSION)) {
                    props.loadFromXML(is);
                }
                else {
                    props.load(is);
                }
            }
        }
        return props;
    }
}
