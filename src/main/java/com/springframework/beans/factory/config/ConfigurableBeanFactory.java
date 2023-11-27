package com.springframework.beans.factory.config;

import com.springframework.beans.PropertyEditorRegistrar;
import com.springframework.beans.TypeConverter;
import com.springframework.beans.config.BeanDefinition;
import com.springframework.beans.factory.BeanFactory;
import com.springframework.beans.factory.HierarchicalBeanFactory;
import com.springframework.core.convert.ConversionService;
import com.springframework.util.StringValueResolver;
import com.sun.istack.internal.Nullable;

import java.util.Set;

/**
 * @author Gary
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory , SingletonBeanRegistry {
    String SCOPE_SINGLETON = "singleton";
    void destroySingletons();
    boolean isFactoryBean(String name) throws RuntimeException;
    String SCOPE_PROTOTYPE = "prototype";
    BeanDefinition getMergedBeanDefinition(String beanName) throws RuntimeException;
    boolean isCacheBeanMetadata();
    TypeConverter getTypeConverter();
    @Nullable
    ConversionService getConversionService();
    @Nullable
    Scope getRegisteredScope(String scopeName);
    @Nullable
    ClassLoader getBeanClassLoader();
    @Nullable
    ClassLoader getTempClassLoader();
    @Nullable
    Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,
                             @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws Exception;
    void setBeanClassLoader(@Nullable ClassLoader beanClassLoader);
    void setBeanExpressionResolver(@Nullable BeanExpressionResolver resolver);
    void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar);
    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);
    BeanExpressionResolver getBeanExpressionResolver();
    String resolveEmbeddedValue(String value);
    void setTempClassLoader(@Nullable ClassLoader tempClassLoader);
    void setConversionService(@Nullable ConversionService conversionService);
    void addEmbeddedValueResolver(StringValueResolver valueResolver);
    int getBeanPostProcessorCount();
    boolean hasEmbeddedValueResolver();
    void registerScope(String scopeName, Scope scope);

}
