package com.springframework.beans.factory.config;

import com.springframework.beans.BeanMetadataElement;
import com.springframework.beans.config.BeanDefinition;
import com.springframework.beans.factory.BeanFactoryUtils;
import com.springframework.util.Assert;
import com.springframework.util.ObjectUtils;
import com.springframework.util.StringUtils;
import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description 包含 BeanDefinition、Bean 的名称以及别名（支持多个）
 * 在解析出来 BeanDefinition 后都会转换成 BeanDefinitionHolder 对象，然后进行注册 ？？
 * <p>
 * |要带着问题去学习,多猜想多验证|
 **/
public class BeanDefinitionHolder implements BeanMetadataElement {
    private final BeanDefinition beanDefinition;

    private final String beanName;

    @Nullable
    private final String[] aliases;


    public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName) {
        this(beanDefinition, beanName, null);
    }

    public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName, @Nullable String[] aliases) {
        Assert.notNull(beanDefinition, "BeanDefinition must not be null");
        Assert.notNull(beanName, "Bean name must not be null");
        this.beanDefinition = beanDefinition;
        this.beanName = beanName;
        this.aliases = aliases;
    }


    /**
     * Return the wrapped BeanDefinition.
     */
    public BeanDefinition getBeanDefinition() {
        return this.beanDefinition;
    }

    /**
     * Return the primary name of the bean, as specified for the bean definition.
     */
    public String getBeanName() {
        return this.beanName;
    }

    /**
     * Return the alias names for the bean, as specified directly for the bean definition.
     *
     * @return the array of alias names, or {@code null} if none
     */
    @Nullable
    public String[] getAliases() {
        return this.aliases;
    }

    /**
     * Expose the bean definition's source object.
     *
     * @see BeanDefinition#getSource()
     */
    @Override
    @Nullable
    public Object getSource() {
        return this.beanDefinition.getSource();
    }


}
