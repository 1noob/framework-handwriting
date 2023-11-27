package com.springframework.context.support;

import com.springframework.beans.factory.BeanNameAware;
import com.springframework.beans.factory.InitializingBean;
import com.springframework.context.ApplicationContext;
import com.springframework.util.Assert;
import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class AbstractRefreshableConfigApplicationContext extends AbstractRefreshableApplicationContext
        implements BeanNameAware, InitializingBean {
    @Nullable
    private String[] configLocations;

    private boolean setIdCalled = false;

    protected String[] getConfigLocations() {
        return (this.configLocations != null ? this.configLocations : getDefaultConfigLocations());
    }

    @Nullable
    protected String[] getDefaultConfigLocations() {
        return null;
    }

    public AbstractRefreshableConfigApplicationContext(@Nullable ApplicationContext parent) {
        super(parent);
    }

    public void setConfigLocations(@Nullable String... locations) {
        if (locations != null) {
            Assert.noNullElements(locations, "Config locations must not be null");
            this.configLocations = new String[locations.length];
            for (int i = 0; i < locations.length; i++) {
                this.configLocations[i] = resolvePath(locations[i]).trim();
            }
        } else {
            this.configLocations = null;
        }
    }

    protected String resolvePath(String path) {
        return getEnvironment().resolveRequiredPlaceholders(path);
    }


}
