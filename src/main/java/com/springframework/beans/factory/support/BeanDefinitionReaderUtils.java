package com.springframework.beans.factory.support;

import com.springframework.beans.config.BeanDefinition;
import com.springframework.beans.factory.BeanFactoryUtils;
import com.springframework.beans.factory.config.BeanDefinitionHolder;
import com.springframework.util.ClassUtils;
import com.springframework.util.ObjectUtils;
import com.springframework.util.StringUtils;
import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class BeanDefinitionReaderUtils {

    /**
     * Separator for generated bean names. If a class name or parent name is not
     * unique, "#1", "#2" etc will be appended, until the name becomes unique.
     */
    public static final String GENERATED_BEAN_NAME_SEPARATOR = BeanFactoryUtils.GENERATED_BEAN_NAME_SEPARATOR;


    /**
     * Create a new GenericBeanDefinition for the given parent name and class name,
     * eagerly loading the bean class if a ClassLoader has been specified.
     * @param parentName the name of the parent bean, if any
     * @param className the name of the bean class, if any
     * @param classLoader the ClassLoader to use for loading bean classes
     * (can be {@code null} to just register bean classes by name)
     * @return the bean definition
     * @throws ClassNotFoundException if the bean class could not be loaded
     */
    public static AbstractBeanDefinition createBeanDefinition(
            @Nullable String parentName, @Nullable String className, @Nullable ClassLoader classLoader) throws ClassNotFoundException {

        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setParentName(parentName);
        if (className != null) {
            if (classLoader != null) {
                bd.setBeanClass(ClassUtils.forName(className, classLoader));
            }
            else {
                bd.setBeanClassName(className);
            }
        }
        return bd;
    }


    public static String generateBeanName(BeanDefinition beanDefinition, BeanDefinitionRegistry registry)
            throws RuntimeException {

        return generateBeanName(beanDefinition, registry, false);
    }


    public static String generateBeanName(
            BeanDefinition definition, BeanDefinitionRegistry registry, boolean isInnerBean)
            throws RuntimeException {

        String generatedBeanName = definition.getBeanClassName();
        if (generatedBeanName == null) {
            if (definition.getParentName() != null) {
                generatedBeanName = definition.getParentName() + "$child";
            }
            else if (definition.getFactoryBeanName() != null) {
                generatedBeanName = definition.getFactoryBeanName() + "$created";
            }
        }
        if (!StringUtils.hasText(generatedBeanName)) {
            throw new RuntimeException("Unnamed bean definition specifies neither " +
                    "'class' nor 'parent' nor 'factory-bean' - can't generate bean name");
        }

        if (isInnerBean) {
            // Inner bean: generate identity hashcode suffix.
            return generatedBeanName + GENERATED_BEAN_NAME_SEPARATOR + ObjectUtils.getIdentityHexString(definition);
        }

        // Top-level bean: use plain class name with unique suffix if necessary.
        return uniqueBeanName(generatedBeanName, registry);
    }

    /**
     * Turn the given bean name into a unique bean name for the given bean factory,
     * appending a unique counter as suffix if necessary.
     * @param beanName the original bean name
     * @param registry the bean factory that the definition is going to be
     * registered with (to check for existing bean names)
     * @return the unique bean name to use
     * @since 5.1
     */
    public static String uniqueBeanName(String beanName, BeanDefinitionRegistry registry) {
        String id = beanName;
        int counter = -1;

        // Increase counter until the id is unique.
        String prefix = beanName + GENERATED_BEAN_NAME_SEPARATOR;
        while (counter == -1 || registry.containsBeanDefinition(id)) {
            counter++;
            id = prefix + counter;
        }
        return id;
    }


    public static void registerBeanDefinition(
            BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry)
            throws RuntimeException {

        // Register bean definition under primary name.
        String beanName = definitionHolder.getBeanName();
        registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());

        // Register aliases for bean name, if any.
        String[] aliases = definitionHolder.getAliases();
        if (aliases != null) {
            for (String alias : aliases) {
                registry.registerAlias(beanName, alias);
            }
        }
    }


}
