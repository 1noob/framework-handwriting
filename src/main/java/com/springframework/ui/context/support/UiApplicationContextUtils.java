package com.springframework.ui.context.support;

import com.springframework.context.ApplicationContext;
import com.springframework.ui.context.HierarchicalThemeSource;
import com.springframework.ui.context.ThemeSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class UiApplicationContextUtils {
    public static final String THEME_SOURCE_BEAN_NAME = "themeSource";
    private static final Log logger = LogFactory.getLog(UiApplicationContextUtils.class);

    public static ThemeSource initThemeSource(ApplicationContext context) throws Exception {
        if (context.containsLocalBean(THEME_SOURCE_BEAN_NAME)) {
            ThemeSource themeSource = context.getBean(THEME_SOURCE_BEAN_NAME, ThemeSource.class);
            // Make ThemeSource aware of parent ThemeSource.
            if (context.getParent() instanceof ThemeSource && themeSource instanceof HierarchicalThemeSource) {
                HierarchicalThemeSource hts = (HierarchicalThemeSource) themeSource;
                if (hts.getParentThemeSource() == null) {
                    // Only set parent context as parent ThemeSource if no parent ThemeSource
                    // registered already.
                    hts.setParentThemeSource((ThemeSource) context.getParent());
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Using ThemeSource [" + themeSource + "]");
            }
            return themeSource;
        }
        else {
            // Use default ThemeSource to be able to accept getTheme calls, either
            // delegating to parent context's default or to local ResourceBundleThemeSource.
            HierarchicalThemeSource themeSource = null;
            if (context.getParent() instanceof ThemeSource) {
                themeSource = new DelegatingThemeSource();
                themeSource.setParentThemeSource((ThemeSource) context.getParent());
            }
            else {
                themeSource = new ResourceBundleThemeSource();
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Unable to locate ThemeSource with name '" + THEME_SOURCE_BEAN_NAME +
                        "': using default [" + themeSource + "]");
            }
            return themeSource;
        }
    }
}
