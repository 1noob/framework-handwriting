package com.springframework.beans.factory.xml;

import com.springframework.util.Assert;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class DelegatingEntityResolver implements EntityResolver {
    /**
     * Suffix for DTD files.
     */
    public static final String DTD_SUFFIX = ".dtd";

    /**
     * Suffix for schema definition files.
     */
    public static final String XSD_SUFFIX = ".xsd";


    private final EntityResolver dtdResolver;

    private final EntityResolver schemaResolver;

    public DelegatingEntityResolver(ClassLoader classLoader) {
        this.dtdResolver = new BeansDtdResolver();
        this.schemaResolver = new PluggableSchemaResolver(classLoader);
    }

    public DelegatingEntityResolver(EntityResolver dtdResolver, EntityResolver schemaResolver) {
        Assert.notNull(dtdResolver, "'dtdResolver' is required");
        Assert.notNull(schemaResolver, "'schemaResolver' is required");
        this.dtdResolver = dtdResolver;
        this.schemaResolver = schemaResolver;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException {

        if (systemId != null) {
            // DTD 模式
            if (systemId.endsWith(DTD_SUFFIX)) {
                return this.dtdResolver.resolveEntity(publicId, systemId);
            }
            // XSD 模式
            else if (systemId.endsWith(XSD_SUFFIX)) {
                return this.schemaResolver.resolveEntity(publicId, systemId);
            }
        }

        // Fall back to the parser's default behavior.
        return null;
    }
}
