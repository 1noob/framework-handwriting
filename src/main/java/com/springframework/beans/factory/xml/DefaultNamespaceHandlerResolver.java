package com.springframework.beans.factory.xml;

import com.springframework.beans.BeanUtils;
import com.springframework.core.io.support.PropertiesLoaderUtils;
import com.springframework.util.Assert;
import com.springframework.util.ClassUtils;
import com.springframework.util.CollectionUtils;
import com.sun.istack.internal.Nullable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class DefaultNamespaceHandlerResolver implements NamespaceHandlerResolver {
    /**
     * The location to look for the mapping files. Can be present in multiple JAR files.
     */
    public static final String DEFAULT_HANDLER_MAPPINGS_LOCATION = "META-INF/spring.handlers";


    /**
     * Logger available to subclasses.
     */
    protected final Log logger = LogFactory.getLog(getClass());

    /**
     * ClassLoader to use for NamespaceHandler classes.
     */
    @Nullable
    private final ClassLoader classLoader;

    /**
     * Resource location to search for.
     */
    private final String handlerMappingsLocation;

    /**
     * Stores the mappings from namespace URI to NamespaceHandler class name / instance.
     */
    @Nullable
    private volatile Map<String, Object> handlerMappings;


    /**
     * Create a new {@code DefaultNamespaceHandlerResolver} using the
     * default mapping file location.
     * <p>This constructor will result in the thread context ClassLoader being used
     * to load resources.
     *
     * @see #DEFAULT_HANDLER_MAPPINGS_LOCATION
     */
    public DefaultNamespaceHandlerResolver() {
        this(null, DEFAULT_HANDLER_MAPPINGS_LOCATION);
    }

    /**
     * Create a new {@code DefaultNamespaceHandlerResolver} using the
     * default mapping file location.
     *
     * @param classLoader the {@link ClassLoader} instance used to load mapping resources
     *                    (may be {@code null}, in which case the thread context ClassLoader will be used)
     * @see #DEFAULT_HANDLER_MAPPINGS_LOCATION
     */
    public DefaultNamespaceHandlerResolver(@Nullable ClassLoader classLoader) {
        this(classLoader, DEFAULT_HANDLER_MAPPINGS_LOCATION);
    }

    /**
     * Create a new {@code DefaultNamespaceHandlerResolver} using the
     * supplied mapping file location.
     *
     * @param classLoader             the {@link ClassLoader} instance used to load mapping resources
     *                                may be {@code null}, in which case the thread context ClassLoader will be used)
     * @param handlerMappingsLocation the mapping file location
     */
    public DefaultNamespaceHandlerResolver(@Nullable ClassLoader classLoader, String handlerMappingsLocation) {
        Assert.notNull(handlerMappingsLocation, "Handler mappings location must not be null");
        this.classLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
        this.handlerMappingsLocation = handlerMappingsLocation;
    }


    /**
     * Locate the {@link NamespaceHandler} for the supplied namespace URI
     * from the configured mappings.
     *
     * @param namespaceUri the relevant namespace URI
     * @return the located {@link NamespaceHandler}, or {@code null} if none found
     * 根据命名空间找到对应的 NamespaceHandler 处理器
     */
    @Override
    @Nullable
    public NamespaceHandler resolve(String namespaceUri) {
        // <1> 获取所有已经配置的命名空间与 NamespaceHandler 处理器的映射
        Map<String, Object> handlerMappings = getHandlerMappings();
        // <2> 根据 `namespaceUri` 命名空间获取 NamespaceHandler 处理器
        Object handlerOrClassName = handlerMappings.get(namespaceUri);
        // <3> 接下来对 NamespaceHandler 进行初始化，因为定义在 `spring.handler` 文件中，可能还没有转换成 Class 类对象
        // <3.1> 不存在
        if (handlerOrClassName == null) {
            return null;
        }
        // <3.2> 已经初始化
        else if (handlerOrClassName instanceof NamespaceHandler) {
            return (NamespaceHandler) handlerOrClassName;
        }
        // <3.3> 需要进行初始化
        else {
            String className = (String) handlerOrClassName;
            try {
                // 获得类，并创建 NamespaceHandler 对象
                Class<?> handlerClass = ClassUtils.forName(className, this.classLoader);
                if (!NamespaceHandler.class.isAssignableFrom(handlerClass)) {
                    throw new RuntimeException("Class [" + className + "] for namespace [" + namespaceUri +
                            "] does not implement the [" + NamespaceHandler.class.getName() + "] interface");
                }
                NamespaceHandler namespaceHandler = (NamespaceHandler) BeanUtils.instantiateClass(handlerClass);
                // 初始化 NamespaceHandler 对象
                namespaceHandler.init();
                // 添加到缓存
                handlerMappings.put(namespaceUri, namespaceHandler);
                return namespaceHandler;
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException("Could not find NamespaceHandler class [" + className +
                        "] for namespace [" + namespaceUri + "]", ex);
            } catch (LinkageError err) {
                throw new RuntimeException("Unresolvable class definition for NamespaceHandler class [" +
                        className + "] for namespace [" + namespaceUri + "]", err);
            }
        }
    }
//	过程如下：
//	1获取所有已经配置的命名空间与 NamespaceHandler 处理器的映射，调用 getHandlerMappings() 方法
//	2根据 namespaceUri 命名空间获取 NamespaceHandler 处理器
//	3接下来对 NamespaceHandler 进行初始化，因为定义在 spring.handler 文件中，可能还没有转换成 Class 类对象
    //	1不存在则返回空对象
    //	2否则，已经初始化则直接返回
    //	3否则，根据 className 创建一个 Class 对象，然后进行实例化，还调用其 init() 方法
//	该方法可以找到命名空间对应的 NamespaceHandler 处理器，关键在于第 1 步如何将 spring.handlers 文件中的内容返回的

    /**
     * 从所有的 META-INF/spring.handlers 文件中获取命名空间与处理器之间的映射
     * Load the specified NamespaceHandler mappings lazily.
     */
    private Map<String, Object> getHandlerMappings() {
        // 双重检查锁，延迟加载
        Map<String, Object> handlerMappings = this.handlerMappings;
        if (handlerMappings == null) {
            synchronized (this) {
                handlerMappings = this.handlerMappings;
                if (handlerMappings == null) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Loading NamespaceHandler mappings from [" + this.handlerMappingsLocation + "]");
                    }
                    try {
                        // 读取 `handlerMappingsLocation`，也就是当前 JVM 环境下所有的 `META-INF/spring.handlers` 文件的内容都会读取到
                        Properties mappings =
                                PropertiesLoaderUtils.loadAllProperties(this.handlerMappingsLocation, this.classLoader);
                        if (logger.isTraceEnabled()) {
                            logger.trace("Loaded NamespaceHandler mappings: " + mappings);
                        }
                        // 初始化到 `handlerMappings` 中
                        handlerMappings = new ConcurrentHashMap<>(mappings.size());
                        CollectionUtils.mergePropertiesIntoMap(mappings, handlerMappings);
                        this.handlerMappings = handlerMappings;
                    } catch (IOException ex) {
                        throw new IllegalStateException(
                                "Unable to load NamespaceHandler mappings from location [" + this.handlerMappingsLocation + "]", ex);
                    }
                }
            }
        }
        return handlerMappings;
    }
//	逻辑不复杂，会读取当前 JVM 环境下所有的 META-INF/spring.handlers 文件，将里面的内容以 key-value 的形式保存在 Map 中返回
//	到这里，对于 Spring XML 文件中的自定义标签的处理逻辑你是不是清晰了
}
