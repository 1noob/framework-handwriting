package com.springframework.context.support;

import com.springframework.beans.config.BeanDefinition;
import com.springframework.beans.factory.BeanFactory;
import com.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.springframework.beans.factory.support.DefaultListableBeanFactory;
import com.springframework.core.env.Environment;
import com.springframework.core.io.Resource;

import java.io.IOException;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class GenericApplicationContext extends AbstractApplicationContext implements BeanDefinitionRegistry {

    private final DefaultListableBeanFactory beanFactory;

    public GenericApplicationContext(DefaultListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public BeanFactory getParentBeanFactory() {
        return null;
    }
    public final DefaultListableBeanFactory getDefaultListableBeanFactory() {
        return this.beanFactory;
    }
    @Override
    public boolean containsLocalBean(String name) {
        return false;
    }

    @Override
    public Object getBean(String name, Object... args) throws Exception {
        return null;
    }

    @Override
    public Object getBean(String name) throws Exception {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws RuntimeException {
        return null;
    }


    @Override
    public boolean containsBean(String name) {
        return false;
    }

    @Override
    public Class<?> getType(String name, boolean allowFactoryBeanInit) throws RuntimeException {
        return null;
    }

    @Override
    public Class<?> getType(String name) throws RuntimeException {
        return null;
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws Exception {
        return false;
    }

    @Override
    public Object getBean(Class<?> returnType, Object[] argsToUse) {
        return null;
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws RuntimeException {

    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return false;
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws Exception {
        return null;
    }

    @Override
    public int getBeanDefinitionCount() {
        return 0;
    }

    @Override
    public boolean isBeanNameInUse(String beanName) {
        return false;
    }

    @Override
    public void registerAlias(String name, String alias) {

    }

    @Override
    public void removeAlias(String alias) {

    }

    @Override
    public boolean isAlias(String name) {
        return false;
    }

    @Override
    public String[] getAliases(String name) {
        return new String[0];
    }


    @Override
    public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
        return this.beanFactory;
    }

    @Override
    protected void closeBeanFactory() {

    }

    @Override
    protected void refreshBeanFactory() throws Exception, IllegalStateException {

    }

    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        return new Resource[0];
    }

    @Override
    public String getApplicationName() {
        return null;
    }

    @Override
    public void publishEvent(Object event) {

    }
}
