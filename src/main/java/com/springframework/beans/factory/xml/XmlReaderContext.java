package com.springframework.beans.factory.xml;

import com.springframework.beans.config.BeanDefinition;
import com.springframework.beans.factory.parsing.ProblemReporter;
import com.springframework.beans.factory.parsing.ReaderContext;
import com.springframework.beans.factory.parsing.ReaderEventListener;
import com.springframework.beans.factory.parsing.SourceExtractor;
import com.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.springframework.core.env.Environment;
import com.springframework.core.io.Resource;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class XmlReaderContext extends ReaderContext {
    private final XmlBeanDefinitionReader reader;
    private final NamespaceHandlerResolver namespaceHandlerResolver;
    public final ClassLoader getBeanClassLoader() {
        return this.reader.getBeanClassLoader();
    }
    public XmlReaderContext(
            Resource resource, ProblemReporter problemReporter,
            ReaderEventListener eventListener, SourceExtractor sourceExtractor,
            XmlBeanDefinitionReader reader, NamespaceHandlerResolver namespaceHandlerResolver) {

        super(resource, problemReporter, eventListener, sourceExtractor);
        this.reader = reader;
        this.namespaceHandlerResolver = namespaceHandlerResolver;
    }
    public String generateBeanName(BeanDefinition beanDefinition) {
        return this.reader.getBeanNameGenerator().generateBeanName(beanDefinition, getRegistry());
    }
    public final NamespaceHandlerResolver getNamespaceHandlerResolver() {
        return this.namespaceHandlerResolver;
    }
    public final BeanDefinitionRegistry getRegistry() {
        return this.reader.getRegistry();
    }

    public final Environment getEnvironment() {
        return this.reader.getEnvironment();
    }
}
