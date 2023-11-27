package com.springframework.ui.context.support;

import com.springframework.beans.factory.BeanClassLoaderAware;
import com.springframework.ui.context.HierarchicalThemeSource;
import com.springframework.ui.context.Theme;
import com.springframework.ui.context.ThemeSource;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class ResourceBundleThemeSource implements HierarchicalThemeSource, BeanClassLoaderAware {
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {

    }

    @Override
    public void setParentThemeSource(ThemeSource parent) {

    }

    @Override
    public ThemeSource getParentThemeSource() {
        return null;
    }

    @Override
    public Theme getTheme(String themeName) {
        return null;
    }
}
