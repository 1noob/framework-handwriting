package com.springframework.beans.factory.support;

import com.springframework.beans.MutablePropertyValues;
import com.springframework.beans.config.BeanDefinition;
import com.springframework.beans.factory.config.BeanDefinitionHolder;
import com.springframework.core.ResolvableType;
import com.sun.istack.internal.Nullable;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @Author 虎哥
 * @Description 表示合并后的 BeanDefinition 对象
 * 在 Spring BeanFactory 初始化 Bean 的前阶段，会根据 BeanDefinition
 * 生成一个 RootBeanDefinition（具有层次性则会进行合并），用于后续实例化和初始化
 * <p>
 * 要带着问题去学习,多猜想多验证
 **/
public class RootBeanDefinition extends AbstractBeanDefinition {
    boolean postProcessed = false;
    boolean constructorArgumentsResolved = false;
    public void setDecoratedDefinition(@Nullable BeanDefinitionHolder decoratedDefinition) {
        this.decoratedDefinition = decoratedDefinition;
    }
    @Nullable
    private Set<String> externallyManagedInitMethods;

    public RootBeanDefinition() {

    }

    public boolean isFactoryMethod(Method candidate) {
        return candidate.getName().equals(getFactoryMethodName());
    }
    @Nullable
    private Set<String> externallyManagedDestroyMethods;
    volatile Boolean isFactoryBean;
    Executable resolvedConstructorOrFactoryMethod;
    public boolean isExternallyManagedInitMethod(String initMethod) {
        synchronized (this.postProcessingLock) {
            return (this.externallyManagedInitMethods != null &&
                    this.externallyManagedInitMethods.contains(initMethod));
        }
    }
    @Nullable
    public Constructor<?>[] getPreferredConstructors() {
        return null;
    }
    /**
     * Determines if the definition needs to be re-merged.
     */
    volatile Boolean beforeInstantiationResolved;
    volatile boolean stale;
    volatile Class<?> resolvedTargetType;
    private BeanDefinitionHolder decoratedDefinition;
    /**
     * Common lock for the two post-processing fields below.
     */
    final Object postProcessingLock = new Object();
    private AnnotatedElement qualifiedElement;
    volatile ResolvableType factoryMethodReturnType;
    boolean allowCaching = true;
    public BeanDefinitionHolder getDecoratedDefinition() {
        return this.decoratedDefinition;
    }
    /**
     * Common lock for the four constructor fields below.
     */
    final Object constructorArgumentLock = new Object();
    public Class<?> getTargetType() {
        if (this.resolvedTargetType != null) {
            return this.resolvedTargetType;
        }
        ResolvableType targetType = this.targetType;
        return (targetType != null ? targetType.resolve() : null);
    }
    boolean isFactoryMethodUnique = false;
    volatile ResolvableType targetType;
    volatile Method factoryMethodToIntrospect;

    public RootBeanDefinition(RootBeanDefinition original) {
        super(original);
        this.decoratedDefinition = original.decoratedDefinition;
        this.qualifiedElement = original.qualifiedElement;
        this.allowCaching = original.allowCaching;
        this.isFactoryMethodUnique = original.isFactoryMethodUnique;
        this.targetType = original.targetType;
        this.factoryMethodToIntrospect = original.factoryMethodToIntrospect;
    }
    public RootBeanDefinition(@Nullable Class<?> beanClass) {
        super();
        setBeanClass(beanClass);
    }

    RootBeanDefinition(BeanDefinition original) {
        super(original);
    }

    @Override
    public RootBeanDefinition cloneBeanDefinition() {
        return new RootBeanDefinition(this);
    }

    @Override
    public String getParentName() {
        return null;
    }

    @Override
    public String getResourceDescription() {
        return null;
    }

    @Override
    public boolean isLazyInit() {
        return false;
    }

    @Override
    public BeanDefinition getOriginatingBeanDefinition() {
        return null;
    }


    @Override
    public boolean isPrimary() {
        return false;
    }

    @Override
    public boolean isAutowireCandidate() {
        return false;
    }

    @Override
    public void setParentName(String parentName) {

    }



    @Override
    public String getScope() {
        return null;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public String getFactoryBeanName() {
        return null;
    }

    @Override
    public String getFactoryMethodName() {
        return null;
    }

    @Override
    public int getRole() {
        return 0;
    }


}
