package com.springframework.context.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.MessageFormat;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class MessageSourceSupport {
    private static final MessageFormat INVALID_MESSAGE_FORMAT = new MessageFormat("");

    /**
     * Logger available to subclasses.
     */
    protected final Log logger = LogFactory.getLog(getClass());

    private boolean alwaysUseMessageFormat = false;
}
