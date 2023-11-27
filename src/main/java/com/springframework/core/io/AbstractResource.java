package com.springframework.core.io;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class AbstractResource implements Resource{
    @Override
    public boolean exists() {
        // Try file existence: can we find the file in the file system?
        if (isFile()) {
            try {
                return getFile().exists();
            }
            catch (IOException ex) {
                Log logger = LogFactory.getLog(getClass());
                if (logger.isDebugEnabled()) {
                    logger.debug("Could not retrieve File for existence check of " + getDescription(), ex);
                }
            }
        }
        // Fall back to stream existence: can we open the stream?
        try {
            getInputStream().close();
            return true;
        }
        catch (Throwable ex) {
            Log logger = LogFactory.getLog(getClass());
            if (logger.isDebugEnabled()) {
                logger.debug("Could not retrieve InputStream for existence check of " + getDescription(), ex);
            }
            return false;
        }
    }
    @Override
    public File getFile() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be resolved to absolute file path");
    }
    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public boolean isOpen() {
        return false;
    }
}
