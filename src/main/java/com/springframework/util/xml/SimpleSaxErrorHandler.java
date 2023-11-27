package com.springframework.util.xml;

import org.apache.commons.logging.Log;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class SimpleSaxErrorHandler implements ErrorHandler {
    private final Log logger;

    public SimpleSaxErrorHandler(Log logger) {
        this.logger = logger;
    }


    @Override
    public void warning(SAXParseException ex) throws SAXException {
        logger.warn("Ignored XML validation warning", ex);
    }

    @Override
    public void error(SAXParseException ex) throws SAXException {
        throw ex;
    }

    @Override
    public void fatalError(SAXParseException ex) throws SAXException {
        throw ex;
    }
}
