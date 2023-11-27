package com.springframework.web.context.support;

import com.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.springframework.context.ApplicationContext;
import com.springframework.context.support.AbstractRefreshableApplicationContext;
import com.springframework.context.support.AbstractRefreshableConfigApplicationContext;
import com.springframework.ui.context.ThemeSource;
import com.springframework.ui.context.support.UiApplicationContextUtils;
import com.springframework.web.context.ConfigurableWebApplicationContext;
import com.springframework.web.context.ServletConfigAware;
import com.springframework.web.context.ServletContextAware;
import com.springframework.web.context.WebApplicationContext;
import com.sun.istack.internal.Nullable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class AbstractRefreshableWebApplicationContext extends AbstractRefreshableConfigApplicationContext
        implements ConfigurableWebApplicationContext {
    private ServletContext servletContext;
    private ServletConfig servletConfig;

    public AbstractRefreshableWebApplicationContext(ApplicationContext parent) {
        super(parent);
    }

    @Override
    public String[] getConfigLocations() {
        return super.getConfigLocations();
    }
    @Override
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        // 添加 ServletContextAwareProcessor 到 BeanFactory 容器中，
        // 该 processor 实现 BeanPostProcessor 接口，主要用于将 ServletContext 传递给实现了 ServletContextAware 接口的 bean
        beanFactory.addBeanPostProcessor(new ServletContextAwareProcessor(this.servletContext, this.servletConfig));
        // 忽略 ServletContextAware、ServletConfigAware，上面的 ServletContextAwareProcessor 已代替
        beanFactory.ignoreDependencyInterface(ServletContextAware.class);
        beanFactory.ignoreDependencyInterface(ServletConfigAware.class);

        // 注册 WEB 应用特定的域（scope）到 beanFactory 中，以便 WebApplicationContext 可以使用它们。
        // 比如'request','session','globalSession','application'
        WebApplicationContextUtils.registerWebApplicationScopes(beanFactory, this.servletContext);
        // 注册 WEB 应用特定的 Environment bean 到 beanFactory 中，以便 WebApplicationContext 可以使用它们
        // 如：'contextParameters','contextAttributes'
        WebApplicationContextUtils.registerEnvironmentBeans(beanFactory, this.servletContext, this.servletConfig);
    }

    private ThemeSource themeSource;

    @Override
    protected void onRefresh() throws Exception {
        this.themeSource = UiApplicationContextUtils.initThemeSource(this);
    }


}
