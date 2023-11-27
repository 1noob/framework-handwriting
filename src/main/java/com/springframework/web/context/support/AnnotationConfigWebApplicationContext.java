package com.springframework.web.context.support;

import com.springframework.beans.factory.BeanFactory;
import com.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.springframework.beans.factory.support.BeanNameGenerator;
import com.springframework.beans.factory.support.DefaultListableBeanFactory;
import com.springframework.context.ApplicationContext;
import com.springframework.context.annotation.*;
import com.springframework.core.io.Resource;
import com.springframework.util.ClassUtils;
import com.springframework.util.StringUtils;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class AnnotationConfigWebApplicationContext extends AbstractRefreshableWebApplicationContext
        implements AnnotationConfigRegistry {

    public AnnotationConfigWebApplicationContext(ApplicationContext parent) {
        super(parent);
    }

    @Override
    public BeanFactory getParentBeanFactory() {
        return null;
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
    public void register(Class<?>... componentClasses) {

    }

    @Override
    public void scan(String... basePackages) {

    }


    @Override
    protected void closeBeanFactory() {

    }


    protected AnnotatedBeanDefinitionReader getAnnotatedBeanDefinitionReader(DefaultListableBeanFactory beanFactory) {
        return new AnnotatedBeanDefinitionReader(beanFactory, getEnvironment());
    }
    protected BeanNameGenerator getBeanNameGenerator() {
        return this.beanNameGenerator;
    }
    private ScopeMetadataResolver scopeMetadataResolver;
    private BeanNameGenerator beanNameGenerator;
    protected ScopeMetadataResolver getScopeMetadataResolver() {
        return this.scopeMetadataResolver;
    }
    private final Set<Class<?>> componentClasses = new LinkedHashSet<>();

    private final Set<String> basePackages = new LinkedHashSet<>();
    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws Exception {
        AnnotatedBeanDefinitionReader reader = getAnnotatedBeanDefinitionReader(beanFactory);
        ClassPathBeanDefinitionScanner scanner = getClassPathBeanDefinitionScanner(beanFactory);

        BeanNameGenerator beanNameGenerator = getBeanNameGenerator();
        if (beanNameGenerator != null) {
            reader.setBeanNameGenerator(beanNameGenerator);
            scanner.setBeanNameGenerator(beanNameGenerator);
            beanFactory.registerSingleton(AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR, beanNameGenerator);
        }

        ScopeMetadataResolver scopeMetadataResolver = getScopeMetadataResolver();
        if (scopeMetadataResolver != null) {
            reader.setScopeMetadataResolver(scopeMetadataResolver);
            scanner.setScopeMetadataResolver(scopeMetadataResolver);
        }

        if (!this.componentClasses.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Registering component classes: [" +
                        StringUtils.collectionToCommaDelimitedString(this.componentClasses) + "]");
            }
            reader.register(ClassUtils.toClassArray(this.componentClasses));
        }

        if (!this.basePackages.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Scanning base packages: [" +
                        StringUtils.collectionToCommaDelimitedString(this.basePackages) + "]");
            }
            // 扫描指定包路径下 @Component 注解的 .class 文件，会解析出 BeanDefinition 对象
            scanner.scan(StringUtils.toStringArray(this.basePackages));
        }

        String[] configLocations = getConfigLocations();
        if (configLocations != null) {
            for (String configLocation : configLocations) {
                try {
                    Class<?> clazz = ClassUtils.forName(configLocation, getClassLoader());
                    if (logger.isTraceEnabled()) {
                        logger.trace("Registering [" + configLocation + "]");
                    }
                    reader.register(clazz);
                } catch (ClassNotFoundException ex) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Could not load class for config location [" + configLocation +
                                "] - trying package scan. " + ex);
                    }
                    int count = scanner.scan(configLocation);
                    if (count == 0 && logger.isDebugEnabled()) {
                        logger.debug("No component classes found for specified class/package [" + configLocation + "]");
                    }
                }
            }
        }
    }

    protected ClassPathBeanDefinitionScanner getClassPathBeanDefinitionScanner(DefaultListableBeanFactory beanFactory) {
        return new ClassPathBeanDefinitionScanner(beanFactory, true, getEnvironment());
    }

    @Override
    public String getApplicationName() {
        return null;
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return false;
    }

    @Override
    public void publishEvent(Object event) {

    }

    @Override
    public void setBeanName(String name) {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
