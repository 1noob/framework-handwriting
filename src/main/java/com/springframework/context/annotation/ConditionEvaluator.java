package com.springframework.context.annotation;

import com.springframework.beans.BeanUtils;
import com.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.springframework.context.ConfigurableApplicationContext;
import com.springframework.core.annotation.AnnotationAwareOrderComparator;
import com.springframework.core.env.Environment;
import com.springframework.core.env.EnvironmentCapable;
import com.springframework.core.env.StandardEnvironment;
import com.springframework.core.io.DefaultResourceLoader;
import com.springframework.core.io.ResourceLoader;
import com.springframework.core.type.AnnotatedTypeMetadata;
import com.springframework.core.type.AnnotationMetadata;
import com.springframework.util.Assert;
import com.springframework.util.ClassUtils;
import com.springframework.util.MultiValueMap;
import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
class ConditionEvaluator {
    private final ConditionContextImpl context;
    public boolean shouldSkip(AnnotatedTypeMetadata metadata) {
        return shouldSkip(metadata, null);
    }
    private List<String[]> getConditionClasses(AnnotatedTypeMetadata metadata) {
        MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(Conditional.class.getName(), true);
        Object values = (attributes != null ? attributes.get("value") : null);
        return (List<String[]>) (values != null ? values : Collections.emptyList());
    }
    public boolean shouldSkip(@Nullable AnnotatedTypeMetadata metadata, @Nullable ConfigurationCondition.ConfigurationPhase phase) {
        if (metadata == null || !metadata.isAnnotated(Conditional.class.getName())) {
            return false;
        }

        if (phase == null) {
            if (metadata instanceof AnnotationMetadata &&
                    ConfigurationClassUtils.isConfigurationCandidate((AnnotationMetadata) metadata)) {
                return shouldSkip(metadata, ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION);
            }
            return shouldSkip(metadata, ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        }

        List<Condition> conditions = new ArrayList<>();
        for (String[] conditionClasses : getConditionClasses(metadata)) {
            for (String conditionClass : conditionClasses) {
                Condition condition = getCondition(conditionClass, this.context.getClassLoader());
                conditions.add(condition);
            }
        }

        AnnotationAwareOrderComparator.sort(conditions);

        for (Condition condition : conditions) {
            ConfigurationCondition.ConfigurationPhase requiredPhase = null;
            if (condition instanceof ConfigurationCondition) {
                requiredPhase = ((ConfigurationCondition) condition).getConfigurationPhase();
            }
            if ((requiredPhase == null || requiredPhase == phase) && !condition.matches(this.context, metadata)) {
                return true;
            }
        }

        return false;
    }
    private Condition getCondition(String conditionClassName, @Nullable ClassLoader classloader) {
        Class<?> conditionClass = ClassUtils.resolveClassName(conditionClassName, classloader);
        return (Condition) BeanUtils.instantiateClass(conditionClass);
    }

    public ConditionEvaluator(@Nullable BeanDefinitionRegistry registry,
                              @Nullable Environment environment, @Nullable ResourceLoader resourceLoader) {

        this.context = new ConditionContextImpl(registry, environment, resourceLoader);
    }

    /**
     * Implementation of a {@link ConditionContext}.
     */
    private static class ConditionContextImpl implements ConditionContext {

        @Nullable
        private final BeanDefinitionRegistry registry;

        @Nullable
        private final ConfigurableListableBeanFactory beanFactory;

        private final Environment environment;

        private final ResourceLoader resourceLoader;

        @Nullable
        private final ClassLoader classLoader;

        public ConditionContextImpl(@Nullable BeanDefinitionRegistry registry,
                                    @Nullable Environment environment, @Nullable ResourceLoader resourceLoader) {

            this.registry = registry;
            this.beanFactory = deduceBeanFactory(registry);
            this.environment = (environment != null ? environment : deduceEnvironment(registry));
            this.resourceLoader = (resourceLoader != null ? resourceLoader : deduceResourceLoader(registry));
            this.classLoader = deduceClassLoader(resourceLoader, this.beanFactory);
        }

        @Nullable
        private ConfigurableListableBeanFactory deduceBeanFactory(@Nullable BeanDefinitionRegistry source) {
            if (source instanceof ConfigurableListableBeanFactory) {
                return (ConfigurableListableBeanFactory) source;
            }
            if (source instanceof ConfigurableApplicationContext) {
                return (((ConfigurableApplicationContext) source).getBeanFactory());
            }
            return null;
        }

        private Environment deduceEnvironment(@Nullable BeanDefinitionRegistry source) {
            if (source instanceof EnvironmentCapable) {
                return ((EnvironmentCapable) source).getEnvironment();
            }
            return new StandardEnvironment();
        }

        private ResourceLoader deduceResourceLoader(@Nullable BeanDefinitionRegistry source) {
            if (source instanceof ResourceLoader) {
                return (ResourceLoader) source;
            }
            return new DefaultResourceLoader();
        }

        @Nullable
        private ClassLoader deduceClassLoader(@Nullable ResourceLoader resourceLoader,
                                              @Nullable ConfigurableListableBeanFactory beanFactory) {

            if (resourceLoader != null) {
                ClassLoader classLoader = resourceLoader.getClassLoader();
                if (classLoader != null) {
                    return classLoader;
                }
            }
            if (beanFactory != null) {
                return beanFactory.getBeanClassLoader();
            }
            return ClassUtils.getDefaultClassLoader();
        }

        @Override
        public BeanDefinitionRegistry getRegistry() {
            Assert.state(this.registry != null, "No BeanDefinitionRegistry available");
            return this.registry;
        }

        @Override
        @Nullable
        public ConfigurableListableBeanFactory getBeanFactory() {
            return this.beanFactory;
        }

        @Override
        public Environment getEnvironment() {
            return this.environment;
        }

        @Override
        public ResourceLoader getResourceLoader() {
            return this.resourceLoader;
        }

        @Override
        @Nullable
        public ClassLoader getClassLoader() {
            return this.classLoader;
        }
    }
}

