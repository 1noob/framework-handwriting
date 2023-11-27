package com.springframework.beans.factory.xml;

import com.springframework.beans.BeanUtils;
import com.springframework.beans.factory.parsing.*;
import com.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import com.springframework.beans.factory.support.BeanDefinitionRegistry;
import com.springframework.core.NamedThreadLocal;
import com.springframework.core.env.Environment;
import com.springframework.core.io.Resource;
import com.springframework.core.io.ResourceLoader;
import com.springframework.core.io.support.EncodedResource;
import com.springframework.util.Assert;
import com.springframework.util.xml.SimpleSaxErrorHandler;
import com.sun.istack.internal.Nullable;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author 虎哥
 * @Description XML 文件资源解析器，解析出 BeanDefinition 配置元信息对象并注册
 * |要带着问题去学习,多猜想多验证|
 **/
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {
    /**
     * 禁用验证模式
     */
    public static final int VALIDATION_NONE = XmlValidationModeDetector.VALIDATION_NONE;

    /**
     * 自动获取验证模式
     */
    public static final int VALIDATION_AUTO = XmlValidationModeDetector.VALIDATION_AUTO;

    /**
     * DTD 验证模式
     */
    public static final int VALIDATION_DTD = XmlValidationModeDetector.VALIDATION_DTD;

    /**
     * XSD 验证模式
     */
    public static final int VALIDATION_XSD = XmlValidationModeDetector.VALIDATION_XSD;

    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    public int loadBeanDefinitions(String location) throws Exception {
        return loadBeanDefinitions(location, null);
    }

    @Override
    public Environment getEnvironment() {
        return null;
    }


    /**
     * 当前线程，正在加载的 EncodedResource 集合。
     */
    private final ThreadLocal<Set<EncodedResource>> resourcesCurrentlyBeingLoaded = new NamedThreadLocal<>(
            "XML bean definition resources currently being loaded");

    @Override
    public int loadBeanDefinitions(Resource resource) throws Exception {
        return loadBeanDefinitions(new EncodedResource(resource));
    }

    public int loadBeanDefinitions(EncodedResource encodedResource) throws Exception {
        Assert.notNull(encodedResource, "EncodedResource must not be null");

        // <1> 获取当前线程正在加载的 Resource 资源集合，添加当前 Resource，防止重复加载
        Set<EncodedResource> currentResources = this.resourcesCurrentlyBeingLoaded.get();
        if (currentResources == null) {
            currentResources = new HashSet<>(4);
            this.resourcesCurrentlyBeingLoaded.set(currentResources);
        }
        // 将当前资源加入记录中。如果已存在，抛出异常，防止循环加载同一资源出现死循环
        if (!currentResources.add(encodedResource)) {
            throw new RuntimeException(
                    "Detected cyclic loading of " + encodedResource + " - check your import definitions!");
        }
        try {
            // <2> 从 Resource 资源获取 InputStream 流对象（支持编码）
            InputStream inputStream = encodedResource.getResource().getInputStream();
            try {
                InputSource inputSource = new InputSource(inputStream);
                if (encodedResource.getEncoding() != null) {
                    inputSource.setEncoding(encodedResource.getEncoding());
                }
                // <3> 【核心】执行加载 Resource 资源过程，解析出 BeanDefinition 进行注册
                return doLoadBeanDefinitions(inputSource, encodedResource.getResource());
            } catch (Exception ex) {
                throw new Exception(
                        "IOException parsing XML document from " + encodedResource.getResource(), ex);
            } finally {
                // 关闭流
                inputStream.close();
            }
        } catch (IOException ex) {
            throw new RuntimeException(
                    "IOException parsing XML document from " + encodedResource.getResource(), ex);
        } finally {
            // <4> 从当前线程移除当前加载的 Resource 对象
            currentResources.remove(encodedResource);
            if (currentResources.isEmpty()) {
                this.resourcesCurrentlyBeingLoaded.remove();
            }
        }
    }


    protected int doLoadBeanDefinitions(InputSource inputSource, Resource resource) throws Exception {
        // <1> 获取 XML Document 实例
        Document doc = doLoadDocument(inputSource, resource);
        // <2> 根据 Document 实例，解析出 BeanDefinition 们并注册，返回注册数量
        int count = registerBeanDefinitions(doc, resource);
        if (logger.isDebugEnabled()) {
            logger.debug("Loaded " + count + " bean definitions from " + resource);
        }
        return count;
    }

    public void setEnvironment(Environment environment) {
        Assert.notNull(environment, "Environment must not be null");
        this.environment = environment;
    }
    private Environment environment;

    /**
     * 验证模式，默认为自动模式。
     */
    private int validationMode = VALIDATION_AUTO;

    private boolean namespaceAware = false;

    protected EntityResolver getEntityResolver() {
        if (this.entityResolver == null) {
            // Determine default EntityResolver to use.
            ResourceLoader resourceLoader = getResourceLoader();
            if (resourceLoader != null) {
                this.entityResolver = new ResourceEntityResolver(resourceLoader);
            } else {
                this.entityResolver = new DelegatingEntityResolver(getBeanClassLoader());
            }
        }
        return this.entityResolver;
    }

    @Override
    public ClassLoader getBeanClassLoader() {
        return null;
    }

    public int getValidationMode() {
        return this.validationMode;
    }

    protected int getValidationModeForResource(Resource resource) {
        int validationModeToUse = getValidationMode();
        if (validationModeToUse != VALIDATION_AUTO) {
            return validationModeToUse;
        }
        int detectedMode = detectValidationMode(resource);
        if (detectedMode != VALIDATION_AUTO) {
            return detectedMode;
        }
        // Hmm, we didn't get a clear indication... Let's assume XSD,
        // since apparently no DTD declaration has been found up until
        // detection stopped (before finding the document's root tag).
        return VALIDATION_XSD;
    }

    protected int detectValidationMode(Resource resource) {
        if (resource.isOpen()) {
            throw new RuntimeException(
                    "Passed-in Resource [" + resource + "] contains an open stream: " +
                            "cannot determine validation mode automatically. Either pass in a Resource " +
                            "that is able to create fresh streams, or explicitly specify the validationMode " +
                            "on your XmlBeanDefinitionReader instance.");
        }

        InputStream inputStream;
        try {
            inputStream = resource.getInputStream();
        } catch (IOException ex) {
            throw new RuntimeException(
                    "Unable to determine validation mode for [" + resource + "]: cannot open InputStream. " +
                            "Did you attempt to load directly from a SAX InputSource without specifying the " +
                            "validationMode on your XmlBeanDefinitionReader instance?", ex);
        }

        try {
            return this.validationModeDetector.detectValidationMode(inputStream);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to determine validation mode for [" +
                    resource + "]: an error occurred whilst reading from the InputStream.", ex);
        }
    }

    /**
     * XML 验证模式探测器
     */
    private final XmlValidationModeDetector validationModeDetector = new XmlValidationModeDetector();

    private ErrorHandler errorHandler = new SimpleSaxErrorHandler(logger);
    private EntityResolver entityResolver;
    private DocumentLoader documentLoader = new DefaultDocumentLoader();

    protected Document doLoadDocument(InputSource inputSource, Resource resource) throws Exception {
        // <3> 通过 DefaultDocumentLoader 根据 Resource 获取一个 Document 对象
        return this.documentLoader.loadDocument(inputSource,
                getEntityResolver(), // <1> 获取 `org.xml.sax.EntityResolver` 实体解析器，ResourceEntityResolver
                this.errorHandler,
                getValidationModeForResource(resource),
                isNamespaceAware()); // <2> 获取 XML 文件验证模式，保证 XML 文件的正确性
    }

    public boolean isNamespaceAware() {
        return this.namespaceAware;
    }

    private Class<? extends BeanDefinitionDocumentReader> documentReaderClass =
            DefaultBeanDefinitionDocumentReader.class;

    protected BeanDefinitionDocumentReader createBeanDefinitionDocumentReader() {
        return BeanUtils.instantiateClass(this.documentReaderClass);
    }

    /**
     * 解析过程中异常处理器
     */
    private ProblemReporter problemReporter = new FailFastProblemReporter();

    private ReaderEventListener eventListener = new EmptyReaderEventListener();
    private NamespaceHandlerResolver namespaceHandlerResolver;
    private SourceExtractor sourceExtractor = new NullSourceExtractor();

    public NamespaceHandlerResolver getNamespaceHandlerResolver() {
        if (this.namespaceHandlerResolver == null) {
            this.namespaceHandlerResolver = createDefaultNamespaceHandlerResolver();
        }
        return this.namespaceHandlerResolver;
    }

    protected NamespaceHandlerResolver createDefaultNamespaceHandlerResolver() {
        ClassLoader cl = (getResourceLoader() != null ? getResourceLoader().getClassLoader() : getBeanClassLoader());
        return new DefaultNamespaceHandlerResolver(cl);
    }

    public XmlReaderContext createReaderContext(Resource resource) {
        return new XmlReaderContext(resource, this.problemReporter, this.eventListener,
                this.sourceExtractor, this, getNamespaceHandlerResolver());
    }

    public int registerBeanDefinitions(Document doc, Resource resource) {
        // <1> 创建 BeanDefinitionDocumentReader 对象
        BeanDefinitionDocumentReader documentReader = createBeanDefinitionDocumentReader();
        // <2> 获取已注册的 BeanDefinition 数量
        int countBefore = getRegistry().getBeanDefinitionCount();
        // <3> 创建 XmlReaderContext 对象（读取 Resource 资源的上下文对象）
        // <4> 根据 Document、XmlReaderContext 解析出所有的 BeanDefinition 并注册
        documentReader.registerBeanDefinitions(doc, createReaderContext(resource));
        // <5> 计算新注册的 BeanDefinition 数量
        return getRegistry().getBeanDefinitionCount() - countBefore;
    }

    public void setEntityResolver(@Nullable EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }
    public void setValidating(boolean validating) {
        this.validationMode = (validating ? VALIDATION_AUTO : VALIDATION_NONE);
        this.namespaceAware = !validating;
    }
}
