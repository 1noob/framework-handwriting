package com.springframework.beans.factory.parsing;

import com.springframework.beans.BeanMetadataElement;
import com.springframework.util.Assert;
import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class AliasDefinition implements BeanMetadataElement {

    private final String beanName;

    private final String alias;

    @Nullable
    private final Object source;


    /**
     * Create a new AliasDefinition.
     * @param beanName the canonical name of the bean
     * @param alias the alias registered for the bean
     */
    public AliasDefinition(String beanName, String alias) {
        this(beanName, alias, null);
    }

    /**
     * Create a new AliasDefinition.
     * @param beanName the canonical name of the bean
     * @param alias the alias registered for the bean
     * @param source the source object (may be {@code null})
     */
    public AliasDefinition(String beanName, String alias, @Nullable Object source) {
        Assert.notNull(beanName, "Bean name must not be null");
        Assert.notNull(alias, "Alias must not be null");
        this.beanName = beanName;
        this.alias = alias;
        this.source = source;
    }


    /**
     * Return the canonical name of the bean.
     */
    public final String getBeanName() {
        return this.beanName;
    }

    /**
     * Return the alias registered for the bean.
     */
    public final String getAlias() {
        return this.alias;
    }

    @Override
    @Nullable
    public final Object getSource() {
        return this.source;
    }

}
