package com.springframework.context.annotation;

import com.springframework.beans.config.BeanDefinition;
import com.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import com.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.springframework.beans.factory.config.BeanDefinitionHolder;
import com.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.springframework.beans.factory.support.DefaultListableBeanFactory;
import com.springframework.beans.factory.support.RootBeanDefinition;
import com.springframework.context.CommonAnnotationBeanPostProcessor;
import com.springframework.context.ConfigurationClassPostProcessor;
import com.springframework.context.event.DefaultEventListenerFactory;
import com.springframework.context.event.EventListenerMethodProcessor;
import com.springframework.context.support.GenericApplicationContext;
import com.springframework.core.annotation.AnnotationAwareOrderComparator;
import com.springframework.core.type.AnnotatedTypeMetadata;
import com.springframework.util.ClassUtils;
import com.sun.istack.internal.Nullable;
import com.sun.org.glassfish.gmbal.Description;

import javax.management.relation.Role;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class AnnotationConfigUtils {
    /**
     * The bean name of the internally managed Configuration annotation processor.
     */
    public static final String CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME =
            "org.springframework.context.annotation.internalConfigurationAnnotationProcessor";
    public static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd) {
        processCommonDefinitionAnnotations(abd, abd.getMetadata());
    }
    static AnnotationAttributes attributesFor(AnnotatedTypeMetadata metadata, Class<?> annotationClass) {
        return attributesFor(metadata, annotationClass.getName());
    }
    static AnnotationAttributes attributesFor(AnnotatedTypeMetadata metadata, String annotationClassName) {
        return AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(annotationClassName, false));
    }
    static BeanDefinitionHolder applyScopedProxyMode(
            ScopeMetadata metadata, BeanDefinitionHolder definition, BeanDefinitionRegistry registry) {

        ScopedProxyMode scopedProxyMode = metadata.getScopedProxyMode();
        if (scopedProxyMode.equals(ScopedProxyMode.NO)) {
            return definition;
        }
        boolean proxyTargetClass = scopedProxyMode.equals(ScopedProxyMode.TARGET_CLASS);
        return ScopedProxyCreator.createScopedProxy(definition, registry, proxyTargetClass);
    }
    static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd, AnnotatedTypeMetadata metadata) {
        AnnotationAttributes lazy = attributesFor(metadata, Lazy.class);
        if (lazy != null) {
            abd.setLazyInit(lazy.getBoolean("value"));
        }
        else if (abd.getMetadata() != metadata) {
            lazy = attributesFor(abd.getMetadata(), Lazy.class);
            if (lazy != null) {
                abd.setLazyInit(lazy.getBoolean("value"));
            }
        }

        if (metadata.isAnnotated(Primary.class.getName())) {
            abd.setPrimary(true);
        }
        AnnotationAttributes dependsOn = attributesFor(metadata, DependsOn.class);
        if (dependsOn != null) {
            abd.setDependsOn(dependsOn.getStringArray("value"));
        }

        AnnotationAttributes role = attributesFor(metadata, Role.class);
        if (role != null) {
            abd.setRole(role.getNumber("value").intValue());
        }
        AnnotationAttributes description = attributesFor(metadata, Description.class);
        if (description != null) {
            abd.setDescription(description.getString("value"));
        }
    }
    public static final String CONFIGURATION_BEAN_NAME_GENERATOR =
            "org.springframework.context.annotation.internalConfigurationBeanNameGenerator";

    /**
     * The bean name of the internally managed Autowired annotation processor.
     */
    public static final String AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME =
            "org.springframework.context.annotation.internalAutowiredAnnotationProcessor";

    /**
     * The bean name of the internally managed Required annotation processor.
     *
     * @deprecated as of 5.1, since no Required processor is registered by default anymore
     */
    @Deprecated
    public static final String REQUIRED_ANNOTATION_PROCESSOR_BEAN_NAME =
            "org.springframework.context.annotation.internalRequiredAnnotationProcessor";

    /**
     * The bean name of the internally managed JSR-250 annotation processor.
     */
    public static final String COMMON_ANNOTATION_PROCESSOR_BEAN_NAME =
            "org.springframework.context.annotation.internalCommonAnnotationProcessor";

    /**
     * The bean name of the internally managed JPA annotation processor.
     */
    public static final String PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME =
            "org.springframework.context.annotation.internalPersistenceAnnotationProcessor";

    private static final String PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME =
            "org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor";

    /**
     * The bean name of the internally managed @EventListener annotation processor.
     */
    public static final String EVENT_LISTENER_PROCESSOR_BEAN_NAME =
            "org.springframework.context.event.internalEventListenerProcessor";

    /**
     * The bean name of the internally managed EventListenerFactory.
     */
    public static final String EVENT_LISTENER_FACTORY_BEAN_NAME =
            "org.springframework.context.event.internalEventListenerFactory";

    private static final boolean jsr250Present;

    private static final boolean jpaPresent;

    static {
        ClassLoader classLoader = AnnotationConfigUtils.class.getClassLoader();
        jsr250Present = ClassUtils.isPresent("javax.annotation.Resource", classLoader);
        jpaPresent = ClassUtils.isPresent("javax.persistence.EntityManagerFactory", classLoader) &&
                ClassUtils.isPresent(PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME, classLoader);
    }

    public static void registerAnnotationConfigProcessors(BeanDefinitionRegistry registry) {
        registerAnnotationConfigProcessors(registry, null);
    }
    private static DefaultListableBeanFactory unwrapDefaultListableBeanFactory(BeanDefinitionRegistry registry) {
        if (registry instanceof DefaultListableBeanFactory) {
            return (DefaultListableBeanFactory) registry;
        }
        else if (registry instanceof GenericApplicationContext) {
            return ((GenericApplicationContext) registry).getDefaultListableBeanFactory();
        }
        else {
            return null;
        }
    }
    public static Set<BeanDefinitionHolder> registerAnnotationConfigProcessors(
            BeanDefinitionRegistry registry, @Nullable Object source) {

        DefaultListableBeanFactory beanFactory = unwrapDefaultListableBeanFactory(registry);
        if (beanFactory != null) {
            if (!(beanFactory.getDependencyComparator() instanceof AnnotationAwareOrderComparator)) {
                beanFactory.setDependencyComparator(AnnotationAwareOrderComparator.INSTANCE);
            }
            if (!(beanFactory.getAutowireCandidateResolver() instanceof ContextAnnotationAutowireCandidateResolver)) {
                beanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
            }
        }

        Set<BeanDefinitionHolder> beanDefs = new LinkedHashSet<>(8);
        // 处理 Spring 应用上下文中的配置类
        if (!registry.containsBeanDefinition(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition(ConfigurationClassPostProcessor.class);
            def.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def, CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME));
        }
        // 处理 @Autowired 以及 @Value 注解
        if (!registry.containsBeanDefinition(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessor.class);
            def.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def, AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME));
        }
        // （条件激活）处理 JSR-250 注解 @Resource，如 @PostConstruct、@PreDestroy 等
        // Check for JSR-250 support, and if present add the CommonAnnotationBeanPostProcessor.
        if (jsr250Present && !registry.containsBeanDefinition(COMMON_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition(CommonAnnotationBeanPostProcessor.class);
            def.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def, COMMON_ANNOTATION_PROCESSOR_BEAN_NAME));
        }
        // Processor 对象（条件激活）处理 JPA 注解场景
        // Check for JPA support, and if present add the PersistenceAnnotationBeanPostProcessor.
        if (jpaPresent && !registry.containsBeanDefinition(PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition();
            try {
                def.setBeanClass(ClassUtils.forName(PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME,
                        AnnotationConfigUtils.class.getClassLoader()));
            } catch (ClassNotFoundException ex) {
                throw new IllegalStateException(
                        "Cannot load optional framework class: " + PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME, ex);
            }
            def.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def, PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME));
        }
        // 处理标注 @EventListener 的 Spring 事件监听方法
        if (!registry.containsBeanDefinition(EVENT_LISTENER_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition(EventListenerMethodProcessor.class);
            def.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def, EVENT_LISTENER_PROCESSOR_BEAN_NAME));
        }
        // 用于 @EventListener 标注的事件监听方法构建成 ApplicationListener 对象
        if (!registry.containsBeanDefinition(EVENT_LISTENER_FACTORY_BEAN_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition(DefaultEventListenerFactory.class);
            def.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def, EVENT_LISTENER_FACTORY_BEAN_NAME));
        }

        return beanDefs;
    }

    private static BeanDefinitionHolder registerPostProcessor(
            BeanDefinitionRegistry registry, RootBeanDefinition definition, String beanName) {

        definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        registry.registerBeanDefinition(beanName, definition);
        return new BeanDefinitionHolder(definition, beanName);
    }


}
