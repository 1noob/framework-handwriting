package com.springframework.beans.factory;

import com.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class BeanFactoryUtils {
    public static final String GENERATED_BEAN_NAME_SEPARATOR = "#";
    private static final Map<String, String> transformedBeanNameCache = new ConcurrentHashMap<>();

    public static boolean isFactoryDereference(String name) {
        return (name != null && name.startsWith(BeanFactory.FACTORY_BEAN_PREFIX));
    }

    public static String transformedBeanName(String name) {
        Assert.notNull(name, "'name' must not be null");
        if (!name.startsWith(BeanFactory.FACTORY_BEAN_PREFIX)) {
            return name;
        }
        // 获取 name 对应的 beanName，
        // 不为 null 则返回 `transformedBeanNameCache` 缓存中对应的 beanName，
        // 为 null 则对 name 进行处理，将前缀 '&' 去除，直至没有 '&'，然后放入 `transformedBeanNameCache` 缓存中，并返回处理后的 beanName
        return transformedBeanNameCache.computeIfAbsent(name, beanName -> {
            do {
                beanName = beanName.substring(BeanFactory.FACTORY_BEAN_PREFIX.length());
            }
            while (beanName.startsWith(BeanFactory.FACTORY_BEAN_PREFIX));
            return beanName;
        });
    }
}
