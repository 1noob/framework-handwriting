package com.springframework.context.annotation;

import com.springframework.beans.config.BeanDefinition;
import com.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import com.springframework.beans.factory.annotation.ScannedGenericBeanDefinition;
import com.springframework.beans.factory.config.BeanDefinitionHolder;
import com.springframework.beans.factory.support.*;
import com.springframework.core.env.Environment;
import com.springframework.core.env.StandardEnvironment;
import com.springframework.core.io.ResourceLoader;
import com.springframework.util.Assert;
import com.springframework.util.PatternMatchUtils;
import com.sun.istack.internal.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider {
    /**
     * BeanDefinition 注册中心 DefaultListableBeanFactory
     */
    private final BeanDefinitionRegistry registry;
    /**
     * BeanDefinition 的默认配置
     */
    private BeanDefinitionDefaults beanDefinitionDefaults = new BeanDefinitionDefaults();

    public int scan(String... basePackages) throws Exception {
        // <1> 获取扫描前的 BeanDefinition 数量
        int beanCountAtScanStart = this.registry.getBeanDefinitionCount();
        // <2> 进行扫描，将过滤出来的所有的 .class 文件生成对应的 BeanDefinition 并注册
        doScan(basePackages);

        // Register annotation config processors, if necessary.
        // <3> 如果 `includeAnnotationConfig` 为 `true`（默认），则注册几个关于注解的 PostProcessor 处理器（关键）
        // 在其他地方也会注册，内部会进行判断，已注册的处理器不会再注册
        if (this.includeAnnotationConfig) {
            AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
        }
        // <4> 返回本次扫描注册的 BeanDefinition 数量
        return (this.registry.getBeanDefinitionCount() - beanCountAtScanStart);
    }

    private String[] autowireCandidatePatterns;

    protected void postProcessBeanDefinition(AbstractBeanDefinition beanDefinition, String beanName) {
        beanDefinition.applyDefaults(this.beanDefinitionDefaults);
        if (this.autowireCandidatePatterns != null) {
            beanDefinition.setAutowireCandidate(PatternMatchUtils.simpleMatch(this.autowireCandidatePatterns, beanName));
        }
    }

    protected Set<BeanDefinitionHolder> doScan(String... basePackages) throws Exception {
        Assert.notEmpty(basePackages, "At least one base package must be specified");
        // <1> 定义个 Set 集合 `beanDefinitions`，用于保存本次扫描成功注册的 BeanDefinition 们
        Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();
        for (String basePackage : basePackages) {// 遍历需要扫描的包名
            // <2> 【核心】扫描包路径，通过 ASM（Java 字节码的操作和分析框架）解析出所有符合条件的 BeanDefinition
            Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
            // <3> 对第 `2` 步解析出来的 BeanDefinition 依次处理，并注册
            for (BeanDefinition candidate : candidates) {
                ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
                // <3.1> 解析出 @Scope 注解的元信息并设置
                candidate.setScope(scopeMetadata.getScopeName());
                // <3.2> 获取或者生成一个的名称 `beanName`
                String beanName = this.beanNameGenerator.generateBeanName(candidate, this.registry);
                // <3.3> 设置相关属性的默认值
                if (candidate instanceof AbstractBeanDefinition) {
                    postProcessBeanDefinition((AbstractBeanDefinition) candidate, beanName);
                }
                // <3.4> 根据这个类的相关注解设置属性值（存在则会覆盖默认值）
                if (candidate instanceof AnnotatedBeanDefinition) {
                    AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
                }
                // <3.5> 检查 beanName 是否已存在，已存在但是不兼容则会抛出异常
                if (checkCandidate(beanName, candidate)) {
                    // <3.6> 将 BeanDefinition 封装成 BeanDefinitionHolder 对象，这里多了一个 `beanName`
                    BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
                    // <3.7> 如果代理模式是 `TARGET_CLASS`，则再创建一个 BeanDefinition 代理对象（重新设置了相关属性），原始 BeanDefinition 已注册
                    definitionHolder =
                            AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
                    // <3.8> 添加至 `beanDefinitions` 集合
                    beanDefinitions.add(definitionHolder);
                    // <3.9> 注册该 BeanDefinition
                    registerBeanDefinition(definitionHolder, this.registry);
                }
            }
        }
        // <4> 返回 `beanDefinitions`（已注册的 BeanDefinition 集合）
        return beanDefinitions;
    }

    protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
    }

    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws Exception {
        if (!this.registry.containsBeanDefinition(beanName)) {
            return true;
        }
        BeanDefinition existingDef = this.registry.getBeanDefinition(beanName);
        BeanDefinition originatingDef = existingDef.getOriginatingBeanDefinition();
        if (originatingDef != null) {
            existingDef = originatingDef;
        }
        if (isCompatible(beanDefinition, existingDef)) {
            return false;
        }
        throw new RuntimeException("Annotation-specified bean name '" + beanName +
                "' for bean class [" + beanDefinition.getBeanClassName() + "] conflicts with existing, " +
                "non-compatible bean definition of same name and class [" + existingDef.getBeanClassName() + "]");
    }

    protected boolean isCompatible(BeanDefinition newDefinition, BeanDefinition existingDefinition) {
        return (!(existingDefinition instanceof ScannedGenericBeanDefinition) ||  // explicitly registered overriding bean
                (newDefinition.getSource() != null && newDefinition.getSource().equals(existingDefinition.getSource())) ||  // scanned same file twice
                newDefinition.equals(existingDefinition));  // scanned equivalent class twice
    }

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters,
                                          Environment environment) {

        this(registry, useDefaultFilters, environment,
                (registry instanceof ResourceLoader ? (ResourceLoader) registry : null));
    }

    private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();
    /**
     * 是否注册几个关于注解的 PostProcessor 处理器
     */
    private boolean includeAnnotationConfig = true;

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters,
                                          Environment environment, @Nullable ResourceLoader resourceLoader) {

        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        this.registry = registry;
        // 注册默认的过滤器，@Component 注解的过滤器（具有层次性）
        if (useDefaultFilters) {
            registerDefaultFilters();
        }
        setEnvironment(environment);
        // 设置资源加载对象，会尝试加载出 CandidateComponentsIndex 对象
        //（保存 `META-INF/spring.components` 文件中的内容，不存在该对象为 `null`）
        setResourceLoader(resourceLoader);
    }

    public void setScopeMetadataResolver(@Nullable ScopeMetadataResolver scopeMetadataResolver) {
        this.scopeMetadataResolver =
                (scopeMetadataResolver != null ? scopeMetadataResolver : new AnnotationScopeMetadataResolver());
    }

    /**
     * Bean 的名称生成器
     */
    private BeanNameGenerator beanNameGenerator = AnnotationBeanNameGenerator.INSTANCE;

    public void setBeanNameGenerator(@Nullable BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator =
                (beanNameGenerator != null ? beanNameGenerator : AnnotationBeanNameGenerator.INSTANCE);
    }

    private Environment environment;
    @Nullable
    private ConditionEvaluator conditionEvaluator;

    /**
     * 资源加载器，默认 PathMatchingResourcePatternResolver
     */
    public void setEnvironment(Environment environment) {
        Assert.notNull(environment, "Environment must not be null");
        this.environment = environment;
        this.conditionEvaluator = null;
    }

    @Override
    public final Environment getEnvironment() {
        if (this.environment == null) {
            this.environment = new StandardEnvironment();
        }
        return this.environment;
    }
}
