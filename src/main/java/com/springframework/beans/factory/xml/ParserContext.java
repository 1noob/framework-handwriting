package com.springframework.beans.factory.xml;

import com.springframework.beans.config.BeanDefinition;
import com.springframework.beans.factory.parsing.ComponentDefinition;
import com.springframework.beans.factory.parsing.CompositeComponentDefinition;
import com.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class ParserContext {
    private final XmlReaderContext readerContext;

    private final BeanDefinitionParserDelegate delegate;

    private BeanDefinition containingBeanDefinition;

    private final Deque<CompositeComponentDefinition> containingComponents = new ArrayDeque<>();


    public ParserContext(XmlReaderContext readerContext, BeanDefinitionParserDelegate delegate) {
        this.readerContext = readerContext;
        this.delegate = delegate;
    }

    public ParserContext(XmlReaderContext readerContext, BeanDefinitionParserDelegate delegate,
                           BeanDefinition containingBeanDefinition) {

        this.readerContext = readerContext;
        this.delegate = delegate;
        this.containingBeanDefinition = containingBeanDefinition;
    }


    public final XmlReaderContext getReaderContext() {
        return this.readerContext;
    }

    public final BeanDefinitionRegistry getRegistry() {
        return this.readerContext.getRegistry();
    }

    public final BeanDefinitionParserDelegate getDelegate() {
        return this.delegate;
    }

    public final BeanDefinition getContainingBeanDefinition() {
        return this.containingBeanDefinition;
    }

    public final boolean isNested() {
        return (this.containingBeanDefinition != null);
    }

    public boolean isDefaultLazyInit() {
        return BeanDefinitionParserDelegate.TRUE_VALUE.equals(this.delegate.getDefaults().getLazyInit());
    }


    public Object extractSource(Object sourceCandidate) {
        return this.readerContext.extractSource(sourceCandidate);
    }


    public CompositeComponentDefinition getContainingComponent() {
        return this.containingComponents.peek();
    }

    public void pushContainingComponent(CompositeComponentDefinition containingComponent) {
        this.containingComponents.push(containingComponent);
    }

    public CompositeComponentDefinition popContainingComponent() {
        return this.containingComponents.pop();
    }

    public void popAndRegisterContainingComponent() {
        registerComponent(popContainingComponent());
    }

    public void registerComponent(ComponentDefinition component) {
        CompositeComponentDefinition containingComponent = getContainingComponent();
        if (containingComponent != null) {
            containingComponent.addNestedComponent(component);
        } else {
            this.readerContext.fireComponentRegistered(component);
        }
    }


}
