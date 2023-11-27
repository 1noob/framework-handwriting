package com.springframework.beans.factory.xml;

import com.springframework.core.io.ClassPathResource;
import com.springframework.core.io.Resource;
import com.springframework.core.io.support.PropertiesLoaderUtils;
import com.springframework.util.Assert;
import com.springframework.util.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class PluggableSchemaResolver implements EntityResolver {

    /**
     * The location of the file that defines schema mappings.
     * Can be present in multiple JAR files.
     */
    public static final String DEFAULT_SCHEMA_MAPPINGS_LOCATION = "META-INF/spring.schemas";


    private static final Log logger = LogFactory.getLog(PluggableSchemaResolver.class);

    private final ClassLoader classLoader;
    /**
     * Schema 文件地址
     */
//	schemaMappingsLocation 属性默认为 META-INF/spring.schemas
    private final String schemaMappingsLocation;

    /**
     * Stores the mapping of schema URL -> local schema path.
     */
    private volatile Map<String, String> schemaMappings;

    public PluggableSchemaResolver(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.schemaMappingsLocation = DEFAULT_SCHEMA_MAPPINGS_LOCATION;
    }

    public PluggableSchemaResolver(ClassLoader classLoader, String schemaMappingsLocation) {
        Assert.hasText(schemaMappingsLocation, "'schemaMappingsLocation' must not be empty");
        this.classLoader = classLoader;
        this.schemaMappingsLocation = schemaMappingsLocation;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws IOException {
        if (logger.isTraceEnabled()) {
            logger.trace("Trying to resolve XML entity with public id [" + publicId +
                    "] and system id [" + systemId + "]");
        }

        if (systemId != null) {
            // <1> 获得对应的 XSD 文件位置，从所有 `META-INF/spring.schemas` 文件中获取对应的本地 XSD 文件位置
            String resourceLocation = getSchemaMappings().get(systemId);
            if (resourceLocation == null && systemId.startsWith("https:")) {
                // Retrieve canonical http schema mapping even for https declaration
                resourceLocation = getSchemaMappings().get("http:" + systemId.substring(6));
            }
            if (resourceLocation != null) {// 本地 XSD 文件位置
                // <2> 创建 ClassPathResource 对象
                Resource resource = new ClassPathResource(resourceLocation, this.classLoader);
                try {
                    // <3> 创建 InputSource 对象，设置 publicId、systemId 属性，返回
                    InputSource source = new InputSource(resource.getInputStream());
                    source.setPublicId(publicId);
                    source.setSystemId(systemId);
                    if (logger.isTraceEnabled()) {
                        logger.trace("Found XML schema [" + systemId + "] in classpath: " + resourceLocation);
                    }
                    return source;
                } catch (FileNotFoundException ex) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Could not find XML schema [" + systemId + "]: " + resource, ex);
                    }
                }
            }
        }

        // Fall back to the parser's default behavior.
        return null;
    }

    /**
     * Load the specified schema mappings lazily.
     * 解析当前 JVM 环境下所有的 META-INF/spring.handlers 文件的内容
     */
    private Map<String, String> getSchemaMappings() {
        Map<String, String> schemaMappings = this.schemaMappings;
        // 双重检查锁，实现 schemaMappings 单例
        if (schemaMappings == null) {
            synchronized (this) {
                schemaMappings = this.schemaMappings;
                if (schemaMappings == null) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Loading schema mappings from [" + this.schemaMappingsLocation + "]");
                    }
                    try {
                        // 读取 `schemaMappingsLocation`，也就是当前 JVM 环境下所有的 `META-INF/spring.handlers` 文件的内容都会读取到
                        Properties mappings =
                                PropertiesLoaderUtils.loadAllProperties(this.schemaMappingsLocation, this.classLoader);
                        if (logger.isTraceEnabled()) {
                            logger.trace("Loaded schema mappings: " + mappings);
                        }
                        // 将 mappings 初始化到 schemaMappings 中
                        schemaMappings = new ConcurrentHashMap<>(mappings.size());
                        CollectionUtils.mergePropertiesIntoMap(mappings, schemaMappings);
                        this.schemaMappings = schemaMappings;
                    }
                    catch (IOException ex) {
                        throw new IllegalStateException(
                                "Unable to load schema mappings from location [" + this.schemaMappingsLocation + "]", ex);
                    }
                }
            }
        }
        return schemaMappings;
    }
}
