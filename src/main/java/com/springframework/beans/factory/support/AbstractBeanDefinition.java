package com.springframework.beans.factory.support;

import com.springframework.beans.BeanMetadataAttributeAccessor;
import com.springframework.beans.MutablePropertyValues;
import com.springframework.beans.config.BeanDefinition;
import com.springframework.beans.factory.config.AutowireCapableBeanFactory;
import com.springframework.beans.factory.config.ConstructorArgumentValues;
import com.springframework.core.io.DescriptiveResource;
import com.springframework.core.io.Resource;
import com.springframework.util.Assert;
import com.springframework.util.ClassUtils;
import com.springframework.util.StringUtils;
import com.sun.istack.internal.Nullable;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @Author 虎哥
 * @Description 包含了一个 Bean 几乎所有的元信息
 * <p>
 * 要带着问题去学习,多猜想多验证
 **/
public abstract class AbstractBeanDefinition extends BeanMetadataAttributeAccessor implements BeanDefinition {
    private boolean synthetic = false;
    public void applyDefaults(BeanDefinitionDefaults defaults) {
        Boolean lazyInit = defaults.getLazyInit();
        if (lazyInit != null) {
            setLazyInit(lazyInit);
        }
        setAutowireMode(defaults.getAutowireMode());
        setDependencyCheck(defaults.getDependencyCheck());
        setInitMethodName(defaults.getInitMethodName());
        setEnforceInitMethod(false);
        setDestroyMethodName(defaults.getDestroyMethodName());
        setEnforceDestroyMethod(false);
    }
    public void setOriginatingBeanDefinition(BeanDefinition originatingBd) {
        this.resource = new BeanDefinitionResource(originatingBd);
    }
    public int getResolvedAutowireMode() {
        if (this.autowireMode == AUTOWIRE_AUTODETECT) {
            // Work out whether to apply setter autowiring or constructor autowiring.
            // If it has a no-arg constructor it's deemed to be setter autowiring,
            // otherwise we'll try constructor autowiring.
            Constructor<?>[] constructors = getBeanClass().getConstructors();
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterCount() == 0) {
                    return AUTOWIRE_BY_TYPE;
                }
            }
            return AUTOWIRE_CONSTRUCTOR;
        }
        else {
            return this.autowireMode;
        }
    }
    /**
     * Constant for the default scope name: {@code ""}, equivalent to singleton
     * status unless overridden from a parent bean definition (if applicable).
     */
    public static final String SCOPE_DEFAULT = "";

    /**
     * Constant that indicates no external autowiring at all.
     *
     * @see #setAutowireMode
     */
    public static final int AUTOWIRE_NO = AutowireCapableBeanFactory.AUTOWIRE_NO;

    /**
     * Constant that indicates autowiring bean properties by name.
     *
     * @see #setAutowireMode
     */
    public static final int AUTOWIRE_BY_NAME = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;

    /**
     * Constant that indicates autowiring bean properties by type.
     *
     * @see #setAutowireMode
     */
    public static final int AUTOWIRE_BY_TYPE = AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;

    /**
     * Constant that indicates autowiring a constructor.
     *
     * @see #setAutowireMode
     */
    public static final int AUTOWIRE_CONSTRUCTOR = AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;

    /**
     * Constant that indicates determining an appropriate autowire strategy
     * through introspection of the bean class.
     *
     * @see #setAutowireMode
     * @deprecated as of Spring 3.0: If you are using mixed autowiring strategies,
     * use annotation-based autowiring for clearer demarcation of autowiring needs.
     */
    @Deprecated
    public static final int AUTOWIRE_AUTODETECT = AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT;

    /**
     * Constant that indicates no dependency check at all.
     *
     * @see #setDependencyCheck
     */
    public static final int DEPENDENCY_CHECK_NONE = 0;
    public Class<?> resolveBeanClass(@Nullable ClassLoader classLoader) throws ClassNotFoundException {
        String className = getBeanClassName();
        if (className == null) {
            return null;
        }
        Class<?> resolvedClass = ClassUtils.forName(className, classLoader);
        this.beanClass = resolvedClass;
        return resolvedClass;
    }
    /**
     * Constant that indicates dependency checking for object references.
     *
     * @see #setDependencyCheck
     */
    public static final int DEPENDENCY_CHECK_OBJECTS = 1;


    public static final int DEPENDENCY_CHECK_SIMPLE = 2;

    /**
     * Constant that indicates dependency checking for all properties
     * (object references as well as "simple" properties).
     *
     * @see #setDependencyCheck
     */
    public static final int DEPENDENCY_CHECK_ALL = 3;

    public abstract AbstractBeanDefinition cloneBeanDefinition();

    public boolean isSynthetic() {
        return this.synthetic;
    }

    private String scope = SCOPE_DEFAULT;
    private boolean abstractFlag = false;
    private String factoryBeanName;
    private String factoryMethodName;
    private Object source;

    protected AbstractBeanDefinition() {
        this(null, null);
    }

    public void addQualifier(AutowireCandidateQualifier qualifier) {
        this.qualifiers.put(qualifier.getTypeName(), qualifier);
    }

    public void validate() throws RuntimeException {
        if (hasMethodOverrides() && getFactoryMethodName() != null) {
            throw new RuntimeException(
                    "Cannot combine factory method with container-generated method overrides: " +
                            "the factory method must create the concrete bean instance.");
        }
        if (hasBeanClass()) {
            prepareMethodOverrides();
        }
    }

    public void prepareMethodOverrides() throws RuntimeException {
        // Check that lookup methods exist and determine their overloaded status.
        // 如果存在 `methodOverrides`
        if (hasMethodOverrides()) {
            // 获取所有的 override method，遍历进行处理
//            getMethodOverrides().getOverrides().forEach(this::prepareMethodOverride);
        }
    }
    @Override
    @Nullable
    public String getBeanClassName() {
        Object beanClassObject = this.beanClass;
        if (beanClassObject instanceof Class) {
            return ((Class<?>) beanClassObject).getName();
        } else {
            return (String) beanClassObject;
        }
    }
    @Nullable
    private String description;

    @Nullable
    private Resource resource;

    @Override
    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    protected AbstractBeanDefinition(@Nullable ConstructorArgumentValues cargs, @Nullable MutablePropertyValues pvs) {
        this.constructorArgumentValues = cargs;
        this.propertyValues = pvs;
    }

    @Override
    public boolean isPrototype() {
        return SCOPE_PROTOTYPE.equals(this.scope);
    }

    @Override
    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setAbstract(boolean abstractFlag) {
        this.abstractFlag = abstractFlag;
    }

    @Override
    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    @Override
    public void setFactoryMethodName(String factoryMethodName) {
        this.factoryMethodName = factoryMethodName;
    }

    @Override
    public void setRole(int role) {
        this.role = role;
    }

    private int role = BeanDefinition.ROLE_APPLICATION;

    @Override
    public void setSource(Object source) {
        this.source = source;
    }

    public void overrideFrom(BeanDefinition other) {
        if (StringUtils.hasLength(other.getBeanClassName())) {
            setBeanClassName(other.getBeanClassName());
        }
        if (StringUtils.hasLength(other.getScope())) {
            setScope(other.getScope());
        }
        setAbstract(other.isAbstract());
        if (StringUtils.hasLength(other.getFactoryBeanName())) {
            setFactoryBeanName(other.getFactoryBeanName());
        }
        if (StringUtils.hasLength(other.getFactoryMethodName())) {
            setFactoryMethodName(other.getFactoryMethodName());
        }
        setRole(other.getRole());
        setSource(other.getSource());
        copyAttributesFrom(other);

        if (other instanceof AbstractBeanDefinition) {
            AbstractBeanDefinition otherAbd = (AbstractBeanDefinition) other;
            if (otherAbd.hasBeanClass()) {
                setBeanClass(otherAbd.getBeanClass());
            }
            if (otherAbd.hasConstructorArgumentValues()) {
                getConstructorArgumentValues().addArgumentValues(other.getConstructorArgumentValues());
            }
            if (otherAbd.hasPropertyValues()) {
                getPropertyValues().addPropertyValues(other.getPropertyValues());
            }
            if (otherAbd.hasMethodOverrides()) {
                getMethodOverrides().addOverrides(otherAbd.getMethodOverrides());
            }
            Boolean lazyInit = otherAbd.getLazyInit();
            if (lazyInit != null) {
                setLazyInit(lazyInit);
            }
            setAutowireMode(otherAbd.getAutowireMode());
            setDependencyCheck(otherAbd.getDependencyCheck());
            setDependsOn(otherAbd.getDependsOn());
            setAutowireCandidate(otherAbd.isAutowireCandidate());
            setPrimary(otherAbd.isPrimary());
            copyQualifiersFrom(otherAbd);
            setInstanceSupplier(otherAbd.getInstanceSupplier());
            setNonPublicAccessAllowed(otherAbd.isNonPublicAccessAllowed());
            setLenientConstructorResolution(otherAbd.isLenientConstructorResolution());
            if (otherAbd.getInitMethodName() != null) {
                setInitMethodName(otherAbd.getInitMethodName());
                setEnforceInitMethod(otherAbd.isEnforceInitMethod());
            }
            if (otherAbd.getDestroyMethodName() != null) {
                setDestroyMethodName(otherAbd.getDestroyMethodName());
                setEnforceDestroyMethod(otherAbd.isEnforceDestroyMethod());
            }
            setSynthetic(otherAbd.isSynthetic());
            setResource(otherAbd.getResource());
        } else {
            getConstructorArgumentValues().addArgumentValues(other.getConstructorArgumentValues());
            getPropertyValues().addPropertyValues(other.getPropertyValues());
            setLazyInit(other.isLazyInit());
            setResourceDescription(other.getResourceDescription());
        }
    }

    protected AbstractBeanDefinition(BeanDefinition original) {
        setParentName(original.getParentName());
        setBeanClassName(original.getBeanClassName());
        setScope(original.getScope());
        setAbstract(original.isAbstract());
        setFactoryBeanName(original.getFactoryBeanName());
        setFactoryMethodName(original.getFactoryMethodName());
        setRole(original.getRole());
        setSource(original.getSource());
        copyAttributesFrom(original);

        if (original instanceof AbstractBeanDefinition) {
            AbstractBeanDefinition originalAbd = (AbstractBeanDefinition) original;
            if (originalAbd.hasBeanClass()) {
                setBeanClass(originalAbd.getBeanClass());
            }
            if (originalAbd.hasConstructorArgumentValues()) {
                setConstructorArgumentValues(new ConstructorArgumentValues(original.getConstructorArgumentValues()));
            }
            if (originalAbd.hasPropertyValues()) {
                setPropertyValues(new MutablePropertyValues(original.getPropertyValues()));
            }
            if (originalAbd.hasMethodOverrides()) {
                setMethodOverrides(new MethodOverrides(originalAbd.getMethodOverrides()));
            }
            Boolean lazyInit = originalAbd.getLazyInit();
            if (lazyInit != null) {
                setLazyInit(lazyInit);
            }
            setAutowireMode(originalAbd.getAutowireMode());
            setDependencyCheck(originalAbd.getDependencyCheck());
            setDependsOn(originalAbd.getDependsOn());
            setAutowireCandidate(originalAbd.isAutowireCandidate());
            setPrimary(originalAbd.isPrimary());
            copyQualifiersFrom(originalAbd);
            setInstanceSupplier(originalAbd.getInstanceSupplier());
            setNonPublicAccessAllowed(originalAbd.isNonPublicAccessAllowed());
            setLenientConstructorResolution(originalAbd.isLenientConstructorResolution());
            setInitMethodName(originalAbd.getInitMethodName());
            setEnforceInitMethod(originalAbd.isEnforceInitMethod());
            setDestroyMethodName(originalAbd.getDestroyMethodName());
            setEnforceDestroyMethod(originalAbd.isEnforceDestroyMethod());
            setSynthetic(originalAbd.isSynthetic());
            setResource(originalAbd.getResource());
        } else {
            setConstructorArgumentValues(new ConstructorArgumentValues(original.getConstructorArgumentValues()));
            setPropertyValues(new MutablePropertyValues(original.getPropertyValues()));
            setLazyInit(original.isLazyInit());
            setResourceDescription(original.getResourceDescription());
        }
    }

    public Class<?> getBeanClass() throws IllegalStateException {
        Object beanClassObject = this.beanClass;
        if (beanClassObject == null) {
            throw new IllegalStateException("No bean class specified on bean definition");
        }
        if (!(beanClassObject instanceof Class)) {
            throw new IllegalStateException(
                    "Bean class name [" + beanClassObject + "] has not been resolved into an actual Class");
        }
        return (Class<?>) beanClassObject;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    /**
     * Specify the bean class name of this bean definition.
     */
    @Override
    public void setBeanClassName(String beanClassName) {
        this.beanClass = beanClassName;
    }

    private volatile Object beanClass;

    public boolean hasBeanClass() {
        return (this.beanClass instanceof Class);
    }

    @Override
    public boolean hasConstructorArgumentValues() {
        return (this.constructorArgumentValues != null && !this.constructorArgumentValues.isEmpty());
    }

    @Override
    public boolean hasPropertyValues() {
        return (this.propertyValues != null && !this.propertyValues.isEmpty());
    }

    public boolean hasMethodOverrides() {
        return !this.methodOverrides.isEmpty();
    }

    public void setMethodOverrides(MethodOverrides methodOverrides) {
        this.methodOverrides = methodOverrides;
    }

    private MethodOverrides methodOverrides = new MethodOverrides();

    public MethodOverrides getMethodOverrides() {
        return this.methodOverrides;
    }

    public Boolean getLazyInit() {
        return this.lazyInit;
    }

    @Override
    public void setDependsOn(String... dependsOn) {
        this.dependsOn = dependsOn;
    }

    public int getDependencyCheck() {
        return this.dependencyCheck;
    }

    @Override
    public void setAutowireCandidate(boolean autowireCandidate) {
        this.autowireCandidate = autowireCandidate;
    }

    private boolean autowireCandidate = true;
    private int dependencyCheck = DEPENDENCY_CHECK_NONE;

    public void setDependencyCheck(int dependencyCheck) {
        this.dependencyCheck = dependencyCheck;
    }

    public void setAutowireMode(int autowireMode) {
        this.autowireMode = autowireMode;
    }

    private boolean primary = false;

    @Override
    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    private final Map<String, AutowireCandidateQualifier> qualifiers = new LinkedHashMap<>();

    public void copyQualifiersFrom(AbstractBeanDefinition source) {
        Assert.notNull(source, "Source must not be null");
        this.qualifiers.putAll(source.qualifiers);
    }

    @Override
    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    public void setEnforceInitMethod(boolean enforceInitMethod) {
        this.enforceInitMethod = enforceInitMethod;
    }

    @Override
    public void setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
    }

    public void setEnforceDestroyMethod(boolean enforceDestroyMethod) {
        this.enforceDestroyMethod = enforceDestroyMethod;
    }

    public int getAutowireMode() {
        return this.autowireMode;
    }

    @Override
    public String[] getDependsOn() {
        return this.dependsOn;
    }

    public Supplier<?> getInstanceSupplier() {
        return this.instanceSupplier;
    }

    public boolean isNonPublicAccessAllowed() {
        return this.nonPublicAccessAllowed;
    }

    public boolean isLenientConstructorResolution() {
        return this.lenientConstructorResolution;
    }

    @Override
    public String getInitMethodName() {
        return this.initMethodName;
    }

    public boolean isEnforceInitMethod() {
        return this.enforceInitMethod;
    }

    @Override
    public String getDestroyMethodName() {
        return this.destroyMethodName;
    }

    public boolean isEnforceDestroyMethod() {
        return this.enforceDestroyMethod;
    }

    public Resource getResource() {
        return this.resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public ConstructorArgumentValues getConstructorArgumentValues() {
        if (this.constructorArgumentValues == null) {
            this.constructorArgumentValues = new ConstructorArgumentValues();
        }
        return this.constructorArgumentValues;
    }

    public void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues) {
        this.constructorArgumentValues = constructorArgumentValues;
    }

    private int autowireMode = AUTOWIRE_NO;
    private String[] dependsOn;
    private String initMethodName;
    private boolean enforceInitMethod = true;
    private String destroyMethodName;
    private boolean enforceDestroyMethod = true;
    private ConstructorArgumentValues constructorArgumentValues;
    private boolean lenientConstructorResolution = true;
    private boolean nonPublicAccessAllowed = true;
    private Supplier<?> instanceSupplier;
    private Boolean lazyInit;
    private MutablePropertyValues propertyValues;

    public void setPropertyValues(MutablePropertyValues propertyValues) {
        this.propertyValues = propertyValues;
    }

    @Override
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public void setSynthetic(boolean synthetic) {
        this.synthetic = synthetic;
    }

    public void setLenientConstructorResolution(boolean lenientConstructorResolution) {
        this.lenientConstructorResolution = lenientConstructorResolution;
    }

    public void setResourceDescription(String resourceDescription) {
        this.resource = (resourceDescription != null ? new DescriptiveResource(resourceDescription) : null);
    }

    public void setNonPublicAccessAllowed(boolean nonPublicAccessAllowed) {
        this.nonPublicAccessAllowed = nonPublicAccessAllowed;
    }

    public void setInstanceSupplier(Supplier<?> instanceSupplier) {
        this.instanceSupplier = instanceSupplier;
    }

    @Override
    public MutablePropertyValues getPropertyValues() {
        if (this.propertyValues == null) {
            this.propertyValues = new MutablePropertyValues();
        }
        return this.propertyValues;
    }

    @Override
    public boolean isSingleton() {
        return "singleton".equals(this.scope) || "".equals(this.scope);
    }
}
