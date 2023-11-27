package com.springframework.context;

import com.springframework.beans.factory.HierarchicalBeanFactory;
import com.springframework.beans.factory.ListableBeanFactory;
import com.springframework.core.env.EnvironmentCapable;
import com.springframework.core.io.support.ResourcePatternResolver;

/**
 * @author Gary
 */
public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory,
        MessageSource, ApplicationEventPublisher, ResourcePatternResolver {
    String getApplicationName();
    ApplicationContext getParent();
    String getDisplayName();
    String getId();
}
