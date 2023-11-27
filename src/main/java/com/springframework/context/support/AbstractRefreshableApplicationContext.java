package com.springframework.context.support;

import com.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.springframework.beans.factory.support.DefaultListableBeanFactory;
import com.springframework.context.ApplicationContext;
import com.sun.istack.internal.Nullable;

import java.io.IOException;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {
    private volatile DefaultListableBeanFactory beanFactory;


    protected final boolean hasBeanFactory() {
        return (this.beanFactory != null);
    }

    @Override
    protected final void refreshBeanFactory() throws Exception {
        // 若已有 BeanFactory ，销毁它的 Bean 们，并销毁 BeanFactory
        if (hasBeanFactory()) {
            destroyBeans();
            closeBeanFactory();
        }
        try {
            // 创建 DefaultListableBeanFactory 对象
            DefaultListableBeanFactory beanFactory = createBeanFactory();
            // 指定序列化编号
            beanFactory.setSerializationId(getId());
            // 定制 BeanFactory 相关属性（是否允许 BeanDefinition 重复定义，是否允许循环依赖，默认都是允许）
            customizeBeanFactory(beanFactory);
            // 加载 BeanDefinition 们
            loadBeanDefinitions(beanFactory);
            this.beanFactory = beanFactory;
        } catch (IOException ex) {
            throw new RuntimeException("I/O error parsing bean definition source for " + getDisplayName(), ex);
        }
    }

    private Boolean allowBeanDefinitionOverriding;

    protected DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory(getInternalParentBeanFactory());
    }

    protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
        if (this.allowBeanDefinitionOverriding != null) {
            beanFactory.setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding);
        }
        if (this.allowCircularReferences != null) {
            beanFactory.setAllowCircularReferences(this.allowCircularReferences);
        }
    }

    private Boolean allowCircularReferences;

    protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory)
            throws Exception, IOException;

    public AbstractRefreshableApplicationContext(@Nullable ApplicationContext parent) {
        super(parent);
    }

    @Override
    public final ConfigurableListableBeanFactory getBeanFactory() {
        DefaultListableBeanFactory beanFactory = this.beanFactory;
        if (beanFactory == null) {
            throw new IllegalStateException("BeanFactory not initialized or already closed - call 'refresh' before accessing beans via the ApplicationContext");
        } else {
            return beanFactory;
        }
    }
}
