package com.springframework.context.support;

import com.springframework.beans.CachedIntrospectionResults;
import com.springframework.beans.factory.BeanFactory;
import com.springframework.beans.factory.config.BeanFactoryPostProcessor;
import com.springframework.beans.factory.config.BeanPostProcessor;
import com.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import com.springframework.beans.factory.support.ResourceEditorRegistrar;
import com.springframework.context.*;
import com.springframework.context.event.ApplicationEventMulticaster;
import com.springframework.context.event.ContextClosedEvent;
import com.springframework.context.event.ContextRefreshedEvent;
import com.springframework.context.event.SimpleApplicationEventMulticaster;
import com.springframework.context.expression.StandardBeanExpressionResolver;
import com.springframework.context.weaving.LoadTimeWeaverAware;
import com.springframework.context.weaving.LoadTimeWeaverAwareProcessor;
import com.springframework.core.Ordered;
import com.springframework.core.PriorityOrdered;
import com.springframework.core.ResolvableType;
import com.springframework.core.annotation.AnnotationUtils;
import com.springframework.core.convert.ConversionService;
import com.springframework.core.env.ConfigurableEnvironment;
import com.springframework.core.env.Environment;
import com.springframework.core.env.StandardEnvironment;
import com.springframework.core.io.DefaultResourceLoader;
import com.springframework.core.io.Resource;
import com.springframework.core.io.ResourceLoader;
import com.springframework.core.io.support.PathMatchingResourcePatternResolver;
import com.springframework.core.io.support.ResourcePatternResolver;
import com.springframework.util.Assert;
import com.springframework.util.ObjectUtils;
import com.springframework.util.ReflectionUtils;
import com.sun.istack.internal.Nullable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {
    /**
     * Logger used by this class. Available to subclasses.
     */
    protected final Log logger = LogFactory.getLog(getClass());
    public static final String MESSAGE_SOURCE_BEAN_NAME = "messageSource";

    public static final String LIFECYCLE_PROCESSOR_BEAN_NAME = "lifecycleProcessor";

    public static final String APPLICATION_EVENT_MULTICASTER_BEAN_NAME = "applicationEventMulticaster";

    static {
        // Eagerly load the ContextClosedEvent class to avoid weird classloader issues
        // on application shutdown in WebLogic 8.1. (Reported by Dustin Woods.)
        ContextClosedEvent.class.getName();
    }
    protected BeanFactory getInternalParentBeanFactory() {
        return (getParent() instanceof ConfigurableApplicationContext ?
                ((ConfigurableApplicationContext) getParent()).getBeanFactory() : getParent());
    }
    @Override
    public String getId() {
        return this.id;
    }
    /**
     * Unique id for this context, if any.
     */
    private String id = ObjectUtils.identityToString(this);

    /**
     * Display name.
     */
    private String displayName = ObjectUtils.identityToString(this);

    /**
     * 父应用上下文
     */
    @Nullable
    private ApplicationContext parent;

    /**
     * 当前应用上下文的环境
     */
    @Nullable
    private ConfigurableEnvironment environment;

    /**
     * BeanFactory 的处理器
     */
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    /**
     * 启动时间
     */
    private long startupDate;

    /**
     * 是否处于激活状态
     */
    private final AtomicBoolean active = new AtomicBoolean();

    /**
     * 是否处于关闭状态
     */
    private final AtomicBoolean closed = new AtomicBoolean();

    /**
     * 启动和销毁时的锁对象
     */
    private final Object startupShutdownMonitor = new Object();
    String SHUTDOWN_HOOK_THREAD_NAME = "SpringContextShutdownHook";
    /**
     * 钩子函数，用于 JVM 关闭时的回调
     */
    @Nullable
    private Thread shutdownHook;

    /**
     * ResourcePatternResolver used by this context.
     */
    private ResourcePatternResolver resourcePatternResolver;

    /**
     * LifecycleProcessor for managing the lifecycle of beans within this context.
     */
    @Nullable
    private LifecycleProcessor lifecycleProcessor;

    /**
     * MessageSource we delegate our implementation of this interface to.
     */
    @Nullable
    private MessageSource messageSource;

    /**
     * 事件广播器
     */
    @Nullable
    private ApplicationEventMulticaster applicationEventMulticaster;

    /**
     * 事件监听器
     */
    private final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet<>();

    /**
     * 早期（Spring 应用上下文还未就绪）注册的时间监听器
     */
    @Nullable
    private Set<ApplicationListener<?>> earlyApplicationListeners;

    /**
     * 早期（Spring 应用上下文还未就绪）发布的事件
     */
    @Nullable
    private Set<ApplicationEvent> earlyApplicationEvents;

    protected ResourcePatternResolver getResourcePatternResolver() {
        return new PathMatchingResourcePatternResolver(this);
    }

    public AbstractApplicationContext() {
        this.resourcePatternResolver = getResourcePatternResolver();
    }

    @Override
    public void setParent(@Nullable ApplicationContext parent) {
        this.parent = parent;
        if (parent != null) {
            Environment parentEnvironment = parent.getEnvironment();
            if (parentEnvironment instanceof ConfigurableEnvironment) {
                getEnvironment().merge((ConfigurableEnvironment) parentEnvironment);
            }
        }
    }

    public AbstractApplicationContext(@Nullable ApplicationContext parent) {
        this();
        setParent(parent);
    }

    @Override
    public ConfigurableEnvironment getEnvironment() {
        if (this.environment == null) {
            this.environment = createEnvironment();
        }
        return this.environment;
    }

    protected ConfigurableEnvironment createEnvironment() {
        return new StandardEnvironment();
    }

    // 发布事件，因为它继承了 ApplicationEventPublisher 事件发布器
    @Override
    public void publishEvent(ApplicationEvent event) {
        publishEvent(event, null);
    }

    protected void publishEvent(Object event, @Nullable ResolvableType eventType) {
        Assert.notNull(event, "Event must not be null");

        // Decorate event as an ApplicationEvent if necessary
        ApplicationEvent applicationEvent;
        if (event instanceof ApplicationEvent) {
            applicationEvent = (ApplicationEvent) event;
        } else {
            // 如果不是 ApplicationEvent 类型的事件，则封装成 PayloadApplicationEvent
            applicationEvent = new PayloadApplicationEvent<>(this, event);
            if (eventType == null) {
                eventType = ((PayloadApplicationEvent<?>) applicationEvent).getResolvableType();
            }
        }

        // Multicast right now if possible - or lazily once the multicaster is initialized
        if (this.earlyApplicationEvents != null) {
            this.earlyApplicationEvents.add(applicationEvent);
        } else {
            // 广播该事件
            getApplicationEventMulticaster().multicastEvent(applicationEvent, eventType);
        }

        // Publish event via parent context as well...
        // 父容器也要发布事件
        if (this.parent != null) {
            if (this.parent instanceof AbstractApplicationContext) {
                ((AbstractApplicationContext) this.parent).publishEvent(event, eventType);
            } else {
                this.parent.publishEvent(event);
            }
        }
    }

    ApplicationEventMulticaster getApplicationEventMulticaster() throws IllegalStateException {
        if (this.applicationEventMulticaster == null) {
            throw new IllegalStateException("ApplicationEventMulticaster not initialized - " +
                    "call 'refresh' before multicasting events via the context: " + this);
        }
        return this.applicationEventMulticaster;
    }

    //	直接往 beanFactoryPostProcessors 添加，BeanFactoryPostProcessor
    //	处理器用于在 Spring 应用上下文刷新阶段对创建好的 BeanFactory 进行后置处理
    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        Assert.notNull(postProcessor, "BeanFactoryPostProcessor must not be null");
        this.beanFactoryPostProcessors.add(postProcessor);
    }

    //	如果事件广播器不为空则将该监听器添加进去，然后再添加到本地的 applicationListeners 中
    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        Assert.notNull(listener, "ApplicationListener must not be null");
        if (this.applicationEventMulticaster != null) {
            this.applicationEventMulticaster.addApplicationListener(listener);
        }
        this.applicationListeners.add(listener);
    }

    /**
     * 刷新上下文，在哪会被调用？
     * 在 **Spring MVC** 中， 方法初始化上下文时，会调用该方法
     */
    @Override
    public void refresh() throws Exception, IllegalStateException {
        // <1> 来个锁，不然 refresh() 还没结束，你又来个启动或销毁容器的操作，那不就乱套了嘛
        synchronized (this.startupShutdownMonitor) {

            // <2> 刷新上下文环境的准备工作，记录下容器的启动时间、标记'已启动'状态、对上下文环境属性进行校验
            prepareRefresh();

            // <3> 创建并初始化一个 BeanFactory 对象 `beanFactory`，会加载出对应的 BeanDefinition 元信息们
            ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

            // <4> 为 `beanFactory` 进行一些准备工作，例如添加几个 BeanPostProcessor，手动注册几个特殊的 Bean
            prepareBeanFactory(beanFactory);

            try {
                // <5> 对 `beanFactory` 在进行一些后期的加工，交由子类进行扩展
                postProcessBeanFactory(beanFactory);

                // <6> 执行 BeanFactoryPostProcessor 处理器，包含 BeanDefinitionRegistryPostProcessor 处理器
                invokeBeanFactoryPostProcessors(beanFactory);

                // <7> 对 BeanPostProcessor 处理器进行初始化，并添加至 BeanFactory 中
                registerBeanPostProcessors(beanFactory);

                // <8> 设置上下文的 MessageSource 对象
                initMessageSource();

                // <9> 设置上下文的 ApplicationEventMulticaster 对象，上下文事件广播器
                initApplicationEventMulticaster();

                // <10> 刷新上下文时再进行一些初始化工作，交由子类进行扩展
                onRefresh();

                // <11> 将所有 ApplicationListener 监听器添加至 `applicationEventMulticaster` 事件广播器，如果已有事件则进行广播
                registerListeners();

                // <12> 设置 ConversionService 类型转换器，**初始化**所有还未初始化的 Bean（不是抽象、单例模式、不是懒加载方式）
                finishBeanFactoryInitialization(beanFactory);

                // <13> 刷新上下文的最后一步工作，会发布 ContextRefreshedEvent 上下文完成刷新事件
                finishRefresh();
            }
            // <14> 如果上面过程出现 BeansException 异常
            catch (Exception ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Exception encountered during context initialization - " +
                            "cancelling refresh attempt: " + ex);
                }
                // <14.1> “销毁” 已注册的单例 Bean
                destroyBeans();

                // <14.2> 设置上下文的 `active` 状态为 `false`
                cancelRefresh(ex);

                // <14.3> 抛出异常
                throw ex;
            }
            // <15> `finally` 代码块
            finally {
                // Reset common introspection caches in Spring's core, since we
                // might not ever need metadata for singleton beans anymore...
                // 清除相关缓存，例如通过反射机制缓存的 Method 和 Field 对象，缓存的注解元数据，缓存的泛型类型对象，缓存的类加载器
                resetCommonCaches();
            }
        }
    }

    protected void resetCommonCaches() {
        ReflectionUtils.clearCache();
        AnnotationUtils.clearCache();
        ResolvableType.clearCache();
        CachedIntrospectionResults.clearClassLoader(getClassLoader());
    }

    protected void cancelRefresh(Exception ex) {
        this.active.set(false);
    }

    protected void finishRefresh() throws Exception {
        // Clear context-level resource caches (such as ASM metadata from scanning).
        // 清除当前 Spring 应用上下文中的缓存，例如通过 ASM（Java 字节码操作和分析框架）扫描出来的元数据
        clearResourceCaches();

        // Initialize lifecycle processor for this context.
        // 初始化 LifecycleProcessor 到当前上下文的属性中
        initLifecycleProcessor();

        // Propagate refresh to lifecycle processor first.
        // 通过 LifecycleProcessor 启动 Lifecycle 生命周期对象
        getLifecycleProcessor().onRefresh();

        // 发布 ContextRefreshedEvent 事件，会通过事件广播器进行广播，可通过自定义监听器在当前 Spring 应用上下文初始化完后进行相关操作
        publishEvent(new ContextRefreshedEvent(this));

        // Participate in LiveBeansView MBean, if active.
        // 如果当前 Spring 应用上下文的 Environment 环境中配置了 'spring.liveBeansView.mbeanDomain'
        // 则向 MBeanServer（JMX 代理层的核心）托管 Live Beans，也就是让 Spring Bean 桥接到 MBeanServer
        LiveBeansView.registerApplicationContext(this);
    }

    LifecycleProcessor getLifecycleProcessor() throws IllegalStateException {
        if (this.lifecycleProcessor == null) {
            throw new IllegalStateException("LifecycleProcessor not initialized - " +
                    "call 'refresh' before invoking lifecycle methods via the context: " + this);
        }
        return this.lifecycleProcessor;
    }

    protected void destroyBeans() {
        getBeanFactory().destroySingletons();
    }

    @Override
    public abstract ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

    protected void initLifecycleProcessor() throws Exception {
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        if (beanFactory.containsLocalBean(LIFECYCLE_PROCESSOR_BEAN_NAME)) {
            this.lifecycleProcessor =
                    beanFactory.getBean(LIFECYCLE_PROCESSOR_BEAN_NAME, LifecycleProcessor.class);
            if (logger.isTraceEnabled()) {
                logger.trace("Using LifecycleProcessor [" + this.lifecycleProcessor + "]");
            }
        } else {
            DefaultLifecycleProcessor defaultProcessor = new DefaultLifecycleProcessor();
            defaultProcessor.setBeanFactory(beanFactory);
            this.lifecycleProcessor = defaultProcessor;
            beanFactory.registerSingleton(LIFECYCLE_PROCESSOR_BEAN_NAME, this.lifecycleProcessor);
            if (logger.isTraceEnabled()) {
                logger.trace("No '" + LIFECYCLE_PROCESSOR_BEAN_NAME + "' bean, using " +
                        "[" + this.lifecycleProcessor.getClass().getSimpleName() + "]");
            }
        }
    }

    //	registerShutdownHook() 方法，向 JVM 注册一个钩子函数，当 JVM 关闭时执行该函数
    @Override
    public void registerShutdownHook() {
        if (this.shutdownHook == null) {
            // No shutdown hook registered yet.
            this.shutdownHook = new Thread(SHUTDOWN_HOOK_THREAD_NAME) {
                @Override
                public void run() {
                    synchronized (startupShutdownMonitor) {
                        doClose();
                    }
                }
            };
            // 为当前的 JVM 运行环境添加一个钩子函数，用于关闭当前上下文
            // 这个钩子函数也就是调用了 doClose() 方法，用于关闭当前 Spring 应用上下文
            Runtime.getRuntime().addShutdownHook(this.shutdownHook);
        }
    }

    protected void doClose() {
        // Check whether an actual close attempt is necessary...
        // Live Beans JMX 撤销托管
        if (this.active.get() && this.closed.compareAndSet(false, true)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Closing " + this);
            }

            LiveBeansView.unregisterApplicationContext(this);

            try {
                // Publish shutdown event.
                // 发布当前 Spring 应用上下文关闭事件
                publishEvent(new ContextClosedEvent(this));
            } catch (Throwable ex) {
                logger.warn("Exception thrown from ApplicationListener handling ContextClosedEvent", ex);
            }

            // Stop all Lifecycle beans, to avoid delays during individual destruction.
            if (this.lifecycleProcessor != null) {
                try {
                    // 关闭 Lifecycle Beans
                    this.lifecycleProcessor.onClose();
                } catch (Throwable ex) {
                    logger.warn("Exception thrown from LifecycleProcessor on context close", ex);
                }
            }

            // Destroy all cached singletons in the context's BeanFactory.
            // 销毁所有的单例 Bean
            destroyBeans();

            // Close the state of this context itself.
            // 关闭底层 BeanFactory 容器
            closeBeanFactory();

            // Let subclasses do some final clean-up if they wish...
            // 提供给子类去实现，用于清理相关资源
            onClose();

            // Reset local application listeners to pre-refresh state.
            if (this.earlyApplicationListeners != null) {
                this.applicationListeners.clear();
                this.applicationListeners.addAll(this.earlyApplicationListeners);
            }

            // Switch to inactive.
            this.active.set(false);
        }
    }

    protected void onClose() {
        // For subclasses: do nothing by default.
    }

    protected abstract void closeBeanFactory();

    // AbstractApplicationContext.java
    protected void prepareRefresh() throws Exception {
        // 设置启动时间
        this.startupDate = System.currentTimeMillis();
        // 设置当前 ApplicationContext 的状态
        this.closed.set(false);
        this.active.set(true);

        // Initialize any placeholder property sources in the context environment.
        // 初始化 ApplicationContext 的 Environment（上下文环境）的相关属性，交由子类去实现，如果是 Web 则会设置 ServletContext 和 ServletConfig
        initPropertySources();

        // Validate that all properties marked as required are resolvable:
        // see ConfigurablePropertyResolver#setRequiredProperties
        // 对属性进行必要的验证
        getEnvironment().validateRequiredProperties();

        // Store pre-refresh ApplicationListeners...
        if (this.earlyApplicationListeners == null) {
            this.earlyApplicationListeners = new LinkedHashSet<>(this.applicationListeners);
        } else {
            // Reset local application listeners to pre-refresh state.
            this.applicationListeners.clear();
            this.applicationListeners.addAll(this.earlyApplicationListeners);
        }

        // Allow for the collection of early ApplicationEvents,
        // to be published once the multicaster is available...
        this.earlyApplicationEvents = new LinkedHashSet<>();
    }

    protected abstract void refreshBeanFactory() throws Exception, IllegalStateException;

    protected void initPropertySources() {
        // For subclasses: do nothing by default.
    }

    protected ConfigurableListableBeanFactory obtainFreshBeanFactory() throws Exception {
        refreshBeanFactory();
        return getBeanFactory();
    }

    protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        // 设置 ClassLoader 类加载器
        beanFactory.setBeanClassLoader(getClassLoader());
        // 设置 BeanExpressionResolver 表达式语言处理器，Spring 3 开始增加了对语言表达式的支持，例如可以使用 #{bean.xxx} 的形式来调用这个 Bean 的属性值
        beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
        // 添加一个默认的 PropertyEditorRegistrar 属性编辑器
        beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment()));

        /*
         * 添加一个 BeanPostProcessor 处理器，ApplicationContextAwareProcessor，初始化 Bean 的**前置**处理
         * 这个 BeanPostProcessor 其实是对几种 Aware 接口的处理，调用其 setXxx 方法
         * 可以跳到 AbstractAutowireCapableBeanFactory 的 initializeBean(...) 方法（调用 Bean 的初始化方法）中看看
         */
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
        // 忽略 Aware 回调接口作为依赖注入接口
        beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
        beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
        beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
        beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);

        // 设置几个自动装配的特殊规则，当你自动注入下面这些类型的 Bean 时，注入的就是右边的值
        // 可以看到 ApplicationContext.class 对应当前对象
        beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
        beanFactory.registerResolvableDependency(ResourceLoader.class, this);
        beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
        beanFactory.registerResolvableDependency(ApplicationContext.class, this);

        // Register early post-processor for detecting inner beans as ApplicationListeners.
        // 添加一个 BeanPostProcessor 处理器，ApplicationListenerDetector，用于装饰监听器
        // 初始化 Bean 的时候，如果是 ApplicationListener 类型且为单例模式，则添加到 Spring 应用上下文
        beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(this));

        // 增加对 AspectJ 的支持，AOP 相关
        if (beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
            beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
            // Set a temporary ClassLoader for type matching.
            beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
        }

        // 注册几个 ApplicationContext 上下文默认的 Bean 对象
        if (!beanFactory.containsLocalBean(ENVIRONMENT_BEAN_NAME)) {
            beanFactory.registerSingleton(ENVIRONMENT_BEAN_NAME, getEnvironment());
        }
        if (!beanFactory.containsLocalBean(SYSTEM_PROPERTIES_BEAN_NAME)) {
            beanFactory.registerSingleton(SYSTEM_PROPERTIES_BEAN_NAME, getEnvironment().getSystemProperties());
        }
        if (!beanFactory.containsLocalBean(SYSTEM_ENVIRONMENT_BEAN_NAME)) {
            beanFactory.registerSingleton(SYSTEM_ENVIRONMENT_BEAN_NAME, getEnvironment().getSystemEnvironment());
        }
    }

    String LOAD_TIME_WEAVER_BEAN_NAME = "loadTimeWeaver";

    /**
     * Name of the {@link Environment} bean in the factory.
     *
     * @since 3.1
     */
    String ENVIRONMENT_BEAN_NAME = "environment";

    /**
     * Name of the System properties bean in the factory.
     *
     * @see java.lang.System#getProperties()
     */
    String SYSTEM_PROPERTIES_BEAN_NAME = "systemProperties";

    /**
     * Name of the System environment bean in the factory.
     *
     * @see java.lang.System#getenv()
     */
    String SYSTEM_ENVIRONMENT_BEAN_NAME = "systemEnvironment";


    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    }

    //	这里借助于 PostProcessorRegistrationDelegate 这个类执行所有 BeanFactoryPostProcessor 处理器，对前面创建的 BeanFactory 进行后置处理
    protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) throws Exception {
        // 执行所有的BeanFactoryPostProcessor处理器
        PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());

        // Detect a LoadTimeWeaver and prepare for weaving, if found in the meantime
        // (e.g. through an @Bean method registered by ConfigurationClassPostProcessor)
        // 在 prepareBeanFactory() 方法中也有相同操作
        if (beanFactory.getTempClassLoader() == null && beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
            beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
            beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
        }
    }

    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return this.beanFactoryPostProcessors;
    }

    protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) throws Exception {
        PostProcessorRegistrationDelegate.registerBeanPostProcessors(beanFactory, this);
    }
    protected MessageSource getInternalParentMessageSource() {
        return (getParent() instanceof AbstractApplicationContext ?
                ((AbstractApplicationContext) getParent()).messageSource : getParent());
    }
    @Override
    @Nullable
    public ApplicationContext getParent() {
        return this.parent;
    }
    protected void initMessageSource() throws Exception {
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        // 如果当前上下文中包含名称为 `messageSource` 的 Bean 对象
        if (beanFactory.containsLocalBean(MESSAGE_SOURCE_BEAN_NAME)) {
            this.messageSource = beanFactory.getBean(MESSAGE_SOURCE_BEAN_NAME, MessageSource.class);
            // 如果有父 ApplicationContext，并且 `messageSource` 为 HierarchicalMessageSource 对象，分级处理的 MessageSource
            if (this.parent != null && this.messageSource instanceof HierarchicalMessageSource) {
                HierarchicalMessageSource hms = (HierarchicalMessageSource) this.messageSource;
                if (hms.getParentMessageSource() == null) {
                    // Only set parent context as parent MessageSource if no parent MessageSource registered already.
                    // 如果 `messageSource` 没有注册父 MessageSource，则设置为父类上下文的的 MessageSource
                    hms.setParentMessageSource(getInternalParentMessageSource());
                }
            }
            if (logger.isTraceEnabled()) {
                logger.trace("Using MessageSource [" + this.messageSource + "]");
            }
        } else {
            // Use empty MessageSource to be able to accept getMessage calls.
            // 使用空 MessageSource
            DelegatingMessageSource dms = new DelegatingMessageSource();
            dms.setParentMessageSource(getInternalParentMessageSource());
            this.messageSource = dms;
            beanFactory.registerSingleton(MESSAGE_SOURCE_BEAN_NAME, this.messageSource);
            if (logger.isTraceEnabled()) {
                logger.trace("No '" + MESSAGE_SOURCE_BEAN_NAME + "' bean, using [" + this.messageSource + "]");
            }
        }
    }

    protected void initApplicationEventMulticaster() throws Exception {
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        // 如果当前上下文中包含名称为 `applicationEventMulticaster` 的 Bean 对象
        if (beanFactory.containsLocalBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)) {
            this.applicationEventMulticaster = beanFactory.getBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, ApplicationEventMulticaster.class);
        } else {
            // 没有则新建 SimpleApplicationEventMulticaster，并将该 Bean 注册至当前上下文
            this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
            beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, this.applicationEventMulticaster);
        }
    }

    protected void onRefresh() throws Exception {
        // For subclasses: do nothing by default.
    }
    /**
     * Return the list of statically specified ApplicationListeners.
     */
    public Collection<ApplicationListener<?>> getApplicationListeners() {
        return this.applicationListeners;
    }
    @Override
    public String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        assertBeanFactoryActive();
        return getBeanFactory().getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }
    protected void assertBeanFactoryActive() {
        if (!this.active.get()) {
            if (this.closed.get()) {
                throw new IllegalStateException(getDisplayName() + " has been closed already");
            }
            else {
                throw new IllegalStateException(getDisplayName() + " has not been refreshed yet");
            }
        }
    }
    @Override
    public String getDisplayName() {
        return this.displayName;
    }
    protected void registerListeners() {
        // <1> 将当前 Spring 应用上下文已有的事件监听器依次添加至事件广播器
        for (ApplicationListener<?> listener : getApplicationListeners()) {
            getApplicationEventMulticaster().addApplicationListener(listener);
        }

        // Do not initialize FactoryBeans here: We need to leave all regular beans
        // uninitialized to let post-processors apply to them!
        // <2> 从底层 BeanFactory 容器中获取所有 ApplicationListener 类型的 beanName 们（还未初始化），然后依次添加至事件广播器
        String[] listenerBeanNames = getBeanNamesForType(ApplicationListener.class, true, false);
        for (String listenerBeanName : listenerBeanNames) {
            getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
        }

        // Publish early application events now that we finally have a multicaster...
        //<3> 至此，已经完成将事件监听器全部添加至事件广播器，接下来将早期的事件通过该事件广播器广播到所有的事件监听器
        // 早期事件：在当前 Spring 应用上下文刷新的过程中已经发布的事件（此时发布不会被监听到，因为事件监听器才刚全部找到，需要到此处通过事件广播器进行广播）
        Set<ApplicationEvent> earlyEventsToProcess = this.earlyApplicationEvents;
        /**
         * 将 `earlyApplicationEvents` 置为 `null`
         * 这里很关键！！！后续发布的事件不再是早期事件，会立即被事件广播器广播。因为当前 Spring 应用中的事件广播器已经就绪了，事件监听器也都获取到了（虽然还没有初始化）
         * 不过在下面广播的时候，如果事件监听器能够处理该事件，则会通过依赖注入的方式初始化该事件监听器
         */
        this.earlyApplicationEvents = null;
        if (earlyEventsToProcess != null) {
            for (ApplicationEvent earlyEvent : earlyEventsToProcess) {
                // 广播该事件，能够处理该事件的事件监听器会被初始化
                getApplicationEventMulticaster().multicastEvent(earlyEvent);
            }
        }
    }
    String CONVERSION_SERVICE_BEAN_NAME = "conversionService";

    protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) throws Exception {
        // 如果底层 BeanFactory 容器包含 ConversionService 类型转换器，则初始化并设置到底层 BeanFactory 容器的属性中
        if (beanFactory.containsBean(CONVERSION_SERVICE_BEAN_NAME) && beanFactory.isTypeMatch(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class)) {
            beanFactory.setConversionService(beanFactory.getBean(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class));
        }

        // 如果底层 BeanFactory 容器没有设置 StringValueResolver 解析器，则添加一个 PropertySourcesPropertyResolver 解析器
        if (!beanFactory.hasEmbeddedValueResolver()) {
            beanFactory.addEmbeddedValueResolver(strVal -> getEnvironment().resolvePlaceholders(strVal));
        }

        // Initialize LoadTimeWeaverAware beans early to allow for registering their transformers early.
        // 提前初始化 LoadTimeWeaverAware 类型的 Bean，AOP 相关
        String[] weaverAwareNames = beanFactory.getBeanNamesForType(LoadTimeWeaverAware.class, false, false);
        for (String weaverAwareName : weaverAwareNames) {
            getBean(weaverAwareName);
        }

        // Stop using the temporary ClassLoader for type matching.
        // 将临时的 ClassLoader 置为 null，它主要用于 AOP
        beanFactory.setTempClassLoader(null);

        // Allow for caching all bean definition metadata, not expecting further changes.
        // 冻结底层 BeanFactory 容器所有的 BeanDefinition，目的是不希望再去修改 BeanDefinition
        beanFactory.freezeConfiguration();

        // 【重点】初始化所有还未初始化的 Bean（不是抽象、单例模式、不是懒加载方式），依赖查找
        beanFactory.preInstantiateSingletons();
    }

    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        return this.resourcePatternResolver.getResources(locationPattern);
    }
    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws Exception {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBean(name, requiredType);
    }
}
