package com.springframework.beans.factory.support;

import com.springframework.core.env.Environment;
import com.springframework.core.env.EnvironmentCapable;
import com.springframework.core.env.StandardEnvironment;
import com.springframework.core.io.Resource;
import com.springframework.core.io.ResourceLoader;
import com.springframework.core.io.support.PathMatchingResourcePatternResolver;
import com.springframework.core.io.support.ResourcePatternResolver;
import com.springframework.util.Assert;
import com.sun.istack.internal.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

/**
 * @Author 虎哥
 * @Description 提供通用的实现，具体的资源加载逻辑在由子类实现
 * 实现了 BeanDefinitionReader 和 EnvironmentCapable 接口
 * 在实现的方法中，最终都会调用 int loadBeanDefinitions(Resource resource) 这个方法，该方法在子类中实现
 * |要带着问题去学习,多猜想多验证|
 **/
@Slf4j
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader, EnvironmentCapable {
    protected final Log logger = LogFactory.getLog(getClass());
    private Environment environment;
    private final BeanDefinitionRegistry registry;
    private ResourceLoader resourceLoader;
    private BeanNameGenerator beanNameGenerator = DefaultBeanNameGenerator.INSTANCE;


    public BeanNameGenerator getBeanNameGenerator() {
        return this.beanNameGenerator;
    }

    public AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        this.registry = registry;

        // Determine ResourceLoader to use.
        if (this.registry instanceof ResourceLoader) {
            this.resourceLoader = (ResourceLoader) this.registry;
        } else {
            this.resourceLoader = new PathMatchingResourcePatternResolver();
        }

        // Inherit Environment if possible
        if (this.registry instanceof EnvironmentCapable) {
            this.environment = ((EnvironmentCapable) this.registry).getEnvironment();
        } else {
            this.environment = new StandardEnvironment();
        }
    }

    @Override
    public ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }
    @Override
    public final BeanDefinitionRegistry  getRegistry() {
        return this.registry;
    }
    @Override
    public int loadBeanDefinitions(String location) throws Exception {
        return loadBeanDefinitions(location, null);
    }

    @Override
    public int loadBeanDefinitions(Resource... resources) throws Exception {
        Assert.notNull(resources, "Resource array must not be null");
        int count = 0;
        for (Resource resource : resources) {
            count += loadBeanDefinitions(resource);
        }
        return count;
    }

    @Override
    public int loadBeanDefinitions(String... locations) throws Exception {
        Assert.notNull(locations, "Location array must not be null");
        int count = 0;
        for (String location : locations) {
            count += loadBeanDefinitions(location);
        }
        return count;
    }

    public int loadBeanDefinitions(String location, Set<Resource> actualResources) throws Exception {
        ResourceLoader resourceLoader = getResourceLoader();
        if (resourceLoader == null) {
            throw new RuntimeException(
                    "Cannot load bean definitions from location [" + location + "]: no ResourceLoader available");
        }

        if (resourceLoader instanceof ResourcePatternResolver) {
            // Resource pattern matching available.
            try {
                Resource[] resources = ((ResourcePatternResolver) resourceLoader).getResources(location);
                int count = loadBeanDefinitions(resources);
                if (actualResources != null) {
                    Collections.addAll(actualResources, resources);
                }
                if (logger.isTraceEnabled()) {
                    logger.trace("Loaded " + count + " bean definitions from location pattern [" + location + "]");
                }
                return count;
            } catch (IOException ex) {
                throw new RuntimeException(
                        "Could not resolve bean definition resource pattern [" + location + "]", ex);
            }
        } else {
            // Can only load single resources by absolute URL.
            Resource resource = resourceLoader.getResource(location);
            int count = loadBeanDefinitions(resource);
            if (actualResources != null) {
                actualResources.add(resource);
            }
            if (logger.isTraceEnabled()) {
                logger.trace("Loaded " + count + " bean definitions from location [" + location + "]");
            }
            return count;
        }
    }
    public void setResourceLoader(@Nullable ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
