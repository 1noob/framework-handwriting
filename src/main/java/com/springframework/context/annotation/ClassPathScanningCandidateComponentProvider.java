package com.springframework.context.annotation;

import com.springframework.beans.config.BeanDefinition;
import com.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import com.springframework.beans.factory.annotation.Lookup;
import com.springframework.beans.factory.annotation.ScannedGenericBeanDefinition;
import com.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.springframework.context.ResourceLoaderAware;
import com.springframework.context.index.CandidateComponentsIndex;
import com.springframework.core.annotation.AnnotationUtils;
import com.springframework.core.env.Environment;
import com.springframework.core.env.EnvironmentCapable;
import com.springframework.core.io.Resource;
import com.springframework.core.io.ResourceLoader;
import com.springframework.core.io.support.PathMatchingResourcePatternResolver;
import com.springframework.core.io.support.ResourcePatternResolver;
import com.springframework.core.type.AnnotationMetadata;
import com.springframework.core.type.classreading.CachingMetadataReaderFactory;
import com.springframework.core.type.classreading.MetadataReader;
import com.springframework.core.type.classreading.MetadataReaderFactory;
import com.springframework.core.type.filter.AnnotationTypeFilter;
import com.springframework.core.type.filter.AssignableTypeFilter;
import com.springframework.core.type.filter.TypeFilter;
import com.springframework.stereotype.Component;
import com.springframework.stereotype.Indexed;
import com.springframework.util.ClassUtils;
import com.sun.istack.internal.Nullable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class ClassPathScanningCandidateComponentProvider implements EnvironmentCapable, ResourceLoaderAware {

    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";


    protected final Log logger = LogFactory.getLog(getClass());

    private String resourcePattern = DEFAULT_RESOURCE_PATTERN;

    /**
     * 包含过滤器
     */
    private final List<TypeFilter> includeFilters = new LinkedList<>();
    /**
     * 排除过滤器
     */
    private final List<TypeFilter> excludeFilters = new LinkedList<>();

    @Nullable
    private Environment environment;

    @Nullable
    private ConditionEvaluator conditionEvaluator;
    /**
     * 资源加载器，默认 PathMatchingResourcePatternResolver
     */
    @Nullable
    private ResourcePatternResolver resourcePatternResolver;
    /**
     * MetadataReader 工厂
     */
    @Nullable
    private MetadataReaderFactory metadataReaderFactory;
    /**
     * 所有 `META-INF/spring.components` 文件的内容
     */
    @Nullable
    private CandidateComponentsIndex componentsIndex;


    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {

    }

    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        /*
         * 2. 扫描包路径，通过 ASM（Java 字节码的操作和分析框架）解析出符合条件的 AnnotatedGenericBeanDefinition 们，并返回
         * 说明：
         * 针对 `1` 解析过程中去扫描指定路径下的 .class 文件的性能问题，从 Spring 5.0 开始新增了一个 @Indexed 注解（新特性），
         * @Component 注解上面就添加了 @Indexed 注解
         *
         * 这里不会去扫描指定路径下的 .class 文件，而是读取所有 `META-INF/spring.components` 文件中符合条件的类名，
         * 直接添加 .class 后缀就是编译文件，而不要去扫描
         *
         * 没在哪看见这样使用过，可以参考 ClassPathScanningCandidateComponentProviderTest#
         * customAnnotationTypeIncludeFilterWithIndex 测试方法
         */
        // `componentsIndex` 不为空，存在 `META-INF/spring.components` 文件并且解析出数据则会创建
        // `includeFilter` 过滤器的元素（注解或类）必须标注 @Indexed 注解
        if (this.componentsIndex != null && indexSupportsIncludeFilters()) {
            return addCandidateComponentsFromIndex(this.componentsIndex, basePackage);
        } else {
            /*
             * 1. 扫描包路径，通过 ASM（Java 字节码的操作和分析框架）解析出符合条件的 ScannedGenericBeanDefinition 们，并返回
             * 首先需要去扫描指定路径下所有的 .class 文件，该过程对于性能有不少的损耗
             * 然后通过 ASM 根据 .class 文件可以获取到这个类的所有元信息，也就可以解析出对应的 BeanDefinition 对象
             */
            return scanCandidateComponents(basePackage);
        }
    }
    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(getEnvironment().resolveRequiredPlaceholders(basePackage));
    }
    private ResourcePatternResolver getResourcePatternResolver() {
        if (this.resourcePatternResolver == null) {
            this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
        }
        return this.resourcePatternResolver;
    }

    public final MetadataReaderFactory getMetadataReaderFactory() {
        if (this.metadataReaderFactory == null) {
            this.metadataReaderFactory = new CachingMetadataReaderFactory();
        }
        return this.metadataReaderFactory;
    }
    private boolean isConditionMatch(MetadataReader metadataReader) {
        if (this.conditionEvaluator == null) {
            this.conditionEvaluator =
                    new ConditionEvaluator(getRegistry(), this.environment, this.resourcePatternResolver);
        }

        return !this.conditionEvaluator.shouldSkip(metadataReader.getAnnotationMetadata());
    }
    protected BeanDefinitionRegistry getRegistry() {
        return null;
    }

    protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
        for (TypeFilter tf : this.excludeFilters) {
            if (tf.match(metadataReader, getMetadataReaderFactory())) {
                return false;
            }
        }
        for (TypeFilter tf : this.includeFilters) {
            if (tf.match(metadataReader, getMetadataReaderFactory())) {
                return isConditionMatch(metadataReader);
            }
        }
        return false;
    }

    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return (metadata.isIndependent() && (metadata.isConcrete() ||
                (metadata.isAbstract() && metadata.hasAnnotatedMethods(Lookup.class.getName()))));
    }

    private Set<BeanDefinition> scanCandidateComponents(String basePackage) {
        // <1> 定义 `candidates` 用于保存符合条件的 BeanDefinition
        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        try {
            // <2> 根据包名生成一个扫描的路径，例如 `classpath*:包路径/**/*.class`
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    resolveBasePackage(basePackage) + '/' + this.resourcePattern;
            // <3> 扫描到包路径下所有的 .class 文件
            Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);
            boolean traceEnabled = logger.isTraceEnabled();
            boolean debugEnabled = logger.isDebugEnabled();
            // <4> 开始对第 `3` 步扫描到的所有 .class 文件（需可读）进行处理，
            // 符合条件的类名会解析出一个 ScannedGenericBeanDefinition
            for (Resource resource : resources) {
                if (traceEnabled) {
                    logger.trace("Scanning " + resource);
                }
                if (resource.isReadable()) {// 文件资源可读
                    try {
                        // <4.1> 根据这个类名找到 `.class` 文件，通过 ASM（Java 字节码操作和分析框架）获取这个类的所有信息
                        // `metadataReader` 对象中包含 ClassMetadata 类元信息和 AnnotationMetadata 注解元信息
                        // 也就是说根据 `.class` 文件就获取到了这个类的元信息，而不是在 JVM 运行时通过 Class 对象进行操作，提高性能
                        MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
                        // <4.2> 根据所有的过滤器判断这个类是否符合条件（例如必须标注 @Component 注解或其派生注解）
                        if (isCandidateComponent(metadataReader)) {
                            // <4.3> 如果符合条件，则创建一个 ScannedGenericBeanDefinition 对象
                            ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
                            // 来源和源对象都是这个 .class 文件资源
                            sbd.setResource(resource);
                            sbd.setSource(resource);
                            /*
                             * <4.4> 再次判断这个类是否符合条件（不是内部类并且是一个具体类）
                             * 具体类：不是接口也不是抽象类，如果是抽象类则需要带有 @Lookup 注解
                             */
                            if (isCandidateComponent(sbd)) {
                                if (debugEnabled) {
                                    logger.debug("Identified candidate component class: " + resource);
                                }
                                // <4.5> 符合条件，则添加至 `candidates` 集合
                                candidates.add(sbd);
                            } else {
                                if (debugEnabled) {
                                    logger.debug("Ignored because not a concrete top-level class: " + resource);
                                }
                            }
                        } else {
                            if (traceEnabled) {
                                logger.trace("Ignored because not matching any filter: " + resource);
                            }
                        }
                    } catch (Throwable ex) {
                        throw new RuntimeException(
                                "Failed to read candidate component class: " + resource, ex);
                    }
                } else {
                    if (traceEnabled) {
                        logger.trace("Ignored because not readable: " + resource);
                    }
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("I/O failure during classpath scanning", ex);
        }
        // <5> 返回 `candidates` 集合
        return candidates;
    }
    private String extractStereotype(TypeFilter filter) {
        if (filter instanceof AnnotationTypeFilter) {
            return ((AnnotationTypeFilter) filter).getAnnotationType().getName();
        }
        if (filter instanceof AssignableTypeFilter) {
            return ((AssignableTypeFilter) filter).getTargetType().getName();
        }
        return null;
    }

    private Set<BeanDefinition> addCandidateComponentsFromIndex(CandidateComponentsIndex index, String basePackage) {
        // <1> 定义 `candidates` 用于保存符合条件的 BeanDefinition
        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        try {
            // <2> 根据过滤器从所有 `META-INF/spring.components` 文件中获取所有符合条件的**类名称**
            Set<String> types = new HashSet<>();
            for (TypeFilter filter : this.includeFilters) {
                // <2.1> 获取过滤注解（或类）的名称（例如 `org.springframework.stereotype.Component`）
                String stereotype = extractStereotype(filter);
                if (stereotype == null) {
                    throw new IllegalArgumentException("Failed to extract stereotype from " + filter);
                }
                // <2.2> 获取注解（或类）对应的条目，并过滤出 `basePackage` 包名下的条目（类的名称）
                types.addAll(index.getCandidateTypes(basePackage, stereotype));
            }
            boolean traceEnabled = logger.isTraceEnabled();
            boolean debugEnabled = logger.isDebugEnabled();
            // <3> 开始对第 `2` 步过滤出来类名进行处理，符合条件的类名会解析出一个 AnnotatedGenericBeanDefinition
            for (String type : types) {
                // <3.1> 根据这个类名找到 `.class` 文件，通过 ASM（Java 字节码操作和分析框架）获取这个类的所有信息
                // `metadataReader` 对象中包含 ClassMetadata 类元信息和 AnnotationMetadata 注解元信息
                // 也就是说根据 `.class` 文件就获取到了这个类的元信息，而不是在 JVM 运行时通过 Class 对象进行操作，提高性能
                MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(type);
                // <3.2> 根据所有的过滤器判断这个类是否符合条件（例如必须标注 @Component 注解或其派生注解）
                if (isCandidateComponent(metadataReader)) {
                    // <3.3> 如果符合条件，则创建一个 AnnotatedGenericBeanDefinition 对象
                    ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
                    sbd.setSource(metadataReader.getResource());
                    /*
                     * <3.4> 再次判断这个类是否符合条件（不是内部类并且是一个具体类）
                     * 具体类：不是接口也不是抽象类，如果是抽象类则需要带有 @Lookup 注解
                     */
                    if (isCandidateComponent(sbd)) {
                        if (debugEnabled) {
                            logger.debug("Using candidate component class from index: " + type);
                        }
                        // <3.5> 符合条件，则添加至 `candidates` 集合
                        candidates.add(sbd);
                    } else {
                        if (debugEnabled) {
                            logger.debug("Ignored because not a concrete top-level class: " + type);
                        }
                    }
                } else {
                    if (traceEnabled) {
                        logger.trace("Ignored because matching an exclude filter: " + type);
                    }
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("I/O failure during classpath scanning", ex);
        }
        // <4> 返回 `candidates` 集合
        return candidates;
    }

    private boolean indexSupportsIncludeFilters() {
        for (TypeFilter includeFilter : this.includeFilters) {
            if (!indexSupportsIncludeFilter(includeFilter)) {
                return false;
            }
        }
        return true;
    }

    private boolean indexSupportsIncludeFilter(TypeFilter filter) {
        if (filter instanceof AnnotationTypeFilter) {
            Class<? extends Annotation> annotation = ((AnnotationTypeFilter) filter).getAnnotationType();
            return (AnnotationUtils.isAnnotationDeclaredLocally(Indexed.class, annotation) ||
                    annotation.getName().startsWith("javax."));
        }
        if (filter instanceof AssignableTypeFilter) {
            Class<?> target = ((AssignableTypeFilter) filter).getTargetType();
            return AnnotationUtils.isAnnotationDeclaredLocally(Indexed.class, target);
        }
        return false;
    }

    protected void registerDefaultFilters() {
        // 添加 @Component 注解的过滤器（具有层次性），@Component 的派生注解都符合条件
        this.includeFilters.add(new AnnotationTypeFilter(Component.class));
        ClassLoader cl = ClassPathScanningCandidateComponentProvider.class.getClassLoader();
        try {
            this.includeFilters.add(new AnnotationTypeFilter(
                    ((Class<? extends Annotation>) ClassUtils.forName("javax.annotation.ManagedBean", cl)), false));
            logger.trace("JSR-250 'javax.annotation.ManagedBean' found and supported for component scanning");
        } catch (ClassNotFoundException ex) {
            // JSR-250 1.1 API (as included in Java EE 6) not available - simply skip.
        }
        try {
            this.includeFilters.add(new AnnotationTypeFilter(
                    ((Class<? extends Annotation>) ClassUtils.forName("javax.inject.Named", cl)), false));
            logger.trace("JSR-330 'javax.inject.Named' annotation found and supported for component scanning");
        } catch (ClassNotFoundException ex) {
            // JSR-330 API not available - simply skip.
        }
    }

    @Override
    public Environment getEnvironment() {
        return null;
    }
}
