package com.springframework.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Properties;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class SpringProperties {
    private static final String PROPERTIES_RESOURCE_LOCATION = "spring.properties";

    private static final Log logger = LogFactory.getLog(SpringProperties.class);

    private static final Properties localProperties = new Properties();

    public static boolean getFlag(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }
    public static String getProperty(String key) {
        String value = localProperties.getProperty(key);
        if (value == null) {
            try {
                value = System.getProperty(key);
            }
            catch (Throwable ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Could not retrieve system property '" + key + "': " + ex);
                }
            }
        }
        return value;
    }

}
