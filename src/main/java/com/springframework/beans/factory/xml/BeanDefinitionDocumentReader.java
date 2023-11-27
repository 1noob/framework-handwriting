package com.springframework.beans.factory.xml;

import org.w3c.dom.Document;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface BeanDefinitionDocumentReader {
    void registerBeanDefinitions(Document doc, XmlReaderContext readerContext)
            throws RuntimeException;
}
