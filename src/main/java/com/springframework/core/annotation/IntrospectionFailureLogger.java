package com.springframework.core.annotation;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/

import com.sun.istack.internal.Nullable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

enum IntrospectionFailureLogger {

    DEBUG {
        @Override
        public boolean isEnabled() {
            return getLogger().isDebugEnabled();
        }

        @Override
        public void log(String message) {
            getLogger().debug(message);
        }
    },

    INFO {
        @Override
        public boolean isEnabled() {
            return getLogger().isInfoEnabled();
        }

        @Override
        public void log(String message) {
            getLogger().info(message);
        }
    };


    @Nullable
    private static Log logger;


    void log(String message, @Nullable Object source, Exception ex) {
        String on = (source != null ? " on " + source : "");
        log(message + on + ": " + ex);
    }

    abstract boolean isEnabled();

    abstract void log(String message);


    private static Log getLogger() {
        Log logger = IntrospectionFailureLogger.logger;
        if (logger == null) {
            logger = LogFactory.getLog(MergedAnnotation.class);
            IntrospectionFailureLogger.logger = logger;
        }
        return logger;
    }

}
