package com.springframework.context.event;

import com.springframework.beans.factory.BeanClassLoaderAware;
import com.springframework.beans.factory.BeanFactory;
import com.springframework.beans.factory.BeanFactoryAware;
import com.springframework.beans.factory.config.ConfigurableBeanFactory;
import com.springframework.context.ApplicationListener;
import com.springframework.core.annotation.AnnotationAwareOrderComparator;
import com.sun.istack.internal.Nullable;

import java.util.*;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class AbstractApplicationEventMulticaster
        implements ApplicationEventMulticaster, BeanClassLoaderAware, BeanFactoryAware {
    @Nullable
    private ClassLoader beanClassLoader;

    @Nullable
    private ConfigurableBeanFactory beanFactory;
    private final ListenerRetriever defaultRetriever = new ListenerRetriever(false);

    private Object retrievalMutex = this.defaultRetriever;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableBeanFactory)) {
            throw new IllegalStateException("Not running in a ConfigurableBeanFactory: " + beanFactory);
        }
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
        if (this.beanClassLoader == null) {
            this.beanClassLoader = this.beanFactory.getBeanClassLoader();
        }
        this.retrievalMutex = this.beanFactory.getSingletonMutex();
    }

    private class ListenerRetriever {

        public final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet<>();

        public final Set<String> applicationListenerBeans = new LinkedHashSet<>();

        private final boolean preFiltered;

        public ListenerRetriever(boolean preFiltered) {
            this.preFiltered = preFiltered;
        }

        public Collection<ApplicationListener<?>> getApplicationListeners() {
            List<ApplicationListener<?>> allListeners = new ArrayList<>(
                    this.applicationListeners.size() + this.applicationListenerBeans.size());
            allListeners.addAll(this.applicationListeners);
            if (!this.applicationListenerBeans.isEmpty()) {
                BeanFactory beanFactory = getBeanFactory();
                for (String listenerBeanName : this.applicationListenerBeans) {
                    try {
                        ApplicationListener<?> listener = beanFactory.getBean(listenerBeanName, ApplicationListener.class);
                        if (this.preFiltered || !allListeners.contains(listener)) {
                            allListeners.add(listener);
                        }
                    } catch (Exception ex) {
                        // Singleton listener instance (without backing bean definition) disappeared -
                        // probably in the middle of the destruction phase
                    }
                }
            }
            if (!this.preFiltered || !this.applicationListenerBeans.isEmpty()) {
                AnnotationAwareOrderComparator.sort(allListeners);
            }
            return allListeners;
        }
    }

    private ConfigurableBeanFactory getBeanFactory() {
        if (this.beanFactory == null) {
            throw new IllegalStateException("ApplicationEventMulticaster cannot retrieve listener beans " +
                    "because it is not associated with a BeanFactory");
        }
        return this.beanFactory;
    }

}
