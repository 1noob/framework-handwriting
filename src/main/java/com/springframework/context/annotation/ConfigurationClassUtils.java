package com.springframework.context.annotation;

import com.springframework.core.type.AnnotationMetadata;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
abstract class ConfigurationClassUtils {
    private static final Log logger = LogFactory.getLog(ConfigurationClassUtils.class);
    private static final Set<String> candidateIndicators = new HashSet<>(8);

    public static boolean isConfigurationCandidate(AnnotationMetadata metadata) {
        // Do not consider an interface or an annotation...
        if (metadata.isInterface()) {
            return false;
        }

        // Any of the typical annotations found?
        for (String indicator : candidateIndicators) {
            if (metadata.isAnnotated(indicator)) {
                return true;
            }
        }

        // Finally, let's look for @Bean methods...
        try {
            return metadata.hasAnnotatedMethods(Bean.class.getName());
        } catch (Throwable ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to introspect @Bean methods on class [" + metadata.getClassName() + "]: " + ex);
            }
            return false;
        }
    }
}
