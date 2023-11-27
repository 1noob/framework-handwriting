package com.springframework.beans.factory.support;

import com.springframework.util.StringUtils;
import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class BeanDefinitionDefaults {

    @Nullable
    private Boolean lazyInit;

    private int autowireMode = AbstractBeanDefinition.AUTOWIRE_NO;

    private int dependencyCheck = AbstractBeanDefinition.DEPENDENCY_CHECK_NONE;

    @Nullable
    private String initMethodName;

    @Nullable
    private String destroyMethodName;
    public Boolean getLazyInit() {
        return this.lazyInit;
    }
    public int getAutowireMode() {
        return this.autowireMode;
    }
    public int getDependencyCheck() {
        return this.dependencyCheck;
    }
    public String getInitMethodName() {
        return this.initMethodName;
    }
    public String getDestroyMethodName() {
        return this.destroyMethodName;
    }


    /**
     * Set whether beans should be lazily initialized by default.
     * <p>If {@code false}, the bean will get instantiated on startup by bean
     * factories that perform eager initialization of singletons.
     * @see AbstractBeanDefinition#setLazyInit
     */
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    /**
     * Set the autowire mode. This determines whether any automagical detection
     * and setting of bean references will happen. Default is AUTOWIRE_NO
     * which means there won't be convention-based autowiring by name or type
     * (however, there may still be explicit annotation-driven autowiring).
     * @param autowireMode the autowire mode to set.
     * Must be one of the constants defined in {@link AbstractBeanDefinition}.
     * @see AbstractBeanDefinition#setAutowireMode
     */
    public void setAutowireMode(int autowireMode) {
        this.autowireMode = autowireMode;
    }

    /**
     * Set the name of the default initializer method.
     * <p>Note that this method is not enforced on all affected bean definitions
     * but rather taken as an optional callback, to be invoked if actually present.
     * @see AbstractBeanDefinition#setInitMethodName
     * @see AbstractBeanDefinition#setEnforceInitMethod
     */
    public void setInitMethodName(@Nullable String initMethodName) {
        this.initMethodName = (StringUtils.hasText(initMethodName) ? initMethodName : null);
    }

    /**
     * Set the name of the default destroy method.
     * <p>Note that this method is not enforced on all affected bean definitions
     * but rather taken as an optional callback, to be invoked if actually present.
     * @see AbstractBeanDefinition#setDestroyMethodName
     * @see AbstractBeanDefinition#setEnforceDestroyMethod
     */
    public void setDestroyMethodName(@Nullable String destroyMethodName) {
        this.destroyMethodName = (StringUtils.hasText(destroyMethodName) ? destroyMethodName : null);
    }

}
