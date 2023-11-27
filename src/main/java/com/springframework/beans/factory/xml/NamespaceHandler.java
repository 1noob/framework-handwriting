package com.springframework.beans.factory.xml;

import com.springframework.beans.config.BeanDefinition;
import com.springframework.beans.factory.config.BeanDefinitionHolder;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public interface NamespaceHandler {
    void init();

    BeanDefinition parse(Element element, ParserContext parserContext);

    BeanDefinitionHolder decorate(Node source, BeanDefinitionHolder definition, ParserContext parserContext);
}
