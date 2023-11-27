package com.springframework.beans.factory;

import com.sun.istack.internal.Nullable;

/**
 * @author Gary
 */
public interface ListableBeanFactory extends BeanFactory {
    String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit);
    boolean containsBeanDefinition(String beanName);

}
