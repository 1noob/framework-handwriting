package com.springframework.context.support;

import com.springframework.beans.config.BeanDefinition;
import com.springframework.beans.factory.config.BeanFactoryPostProcessor;
import com.springframework.beans.factory.config.BeanPostProcessor;
import com.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.springframework.beans.factory.support.*;
import com.springframework.core.OrderComparator;
import com.springframework.core.Ordered;
import com.springframework.core.PriorityOrdered;
import com.sun.istack.internal.Nullable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class PostProcessorRegistrationDelegate {
    private PostProcessorRegistrationDelegate() {
	}

	public static void invokeBeanFactoryPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) throws Exception {

		// Invoke BeanDefinitionRegistryPostProcessors first, if any.
		Set<String> processedBeans = new HashSet<>();
		// <1> 执行当前 Spring 应用上下文和底层 BeanFactory 容器中的 BeanFactoryPostProcessor、BeanDefinitionRegistryPostProcessor 们的处理
		if (beanFactory instanceof BeanDefinitionRegistry) {
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
			List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
			List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();
			// <1.1> 先遍历当前 Spring 应用上下文中的 `beanFactoryPostProcessors`，如果是 BeanDefinitionRegistryPostProcessor 类型则进行处理
			for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
				if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
					BeanDefinitionRegistryPostProcessor registryProcessor =
							(BeanDefinitionRegistryPostProcessor) postProcessor;
					// 执行
					registryProcessor.postProcessBeanDefinitionRegistry(registry);
					// 添加，以供后续执行其他 `postProcessBeanFactory(registry)` 方法
					registryProcessors.add(registryProcessor);
				}
				else {
					regularPostProcessors.add(postProcessor);
				}
			}

			// Do not initialize FactoryBeans here: We need to leave all regular beans
			// uninitialized to let the bean factory post-processors apply to them!
			// Separate between BeanDefinitionRegistryPostProcessors that implement
			// PriorityOrdered, Ordered, and the rest.
			// 临时变量，用于临时保存 BeanFactory 容器中的 BeanDefinitionRegistryPostProcessor 对象
			List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

			// First, invoke the BeanDefinitionRegistryPostProcessors that implement PriorityOrdered.
			// <1.2> 获取底层 BeanFactory 容器中所有 BeanDefinitionRegistryPostProcessor 类型的 Bean 们，遍历进行处理
			String[] postProcessorNames =
					beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				// 如果实现了 PriorityOrdered 接口，则获取到对应的 Bean
				if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
					// 初始化
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					processedBeans.add(ppName);
				}
			}
			// 排序
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			// 临时保存起来
			registryProcessors.addAll(currentRegistryProcessors);
			// 执行
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			// 清理
			currentRegistryProcessors.clear();

			// Next, invoke the BeanDefinitionRegistryPostProcessors that implement Ordered.
			// <1.3> 获取底层 BeanFactory 容器中所有 BeanDefinitionRegistryPostProcessor 类型的 Bean 们，遍历进行处理
			postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				// 如果实现了 Ordered 接口并且没有执行过，则获取到对应的 Bean
				if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
					// 初始化
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					processedBeans.add(ppName);
				}
			}
			// 排序
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			// 临时保存起来
			registryProcessors.addAll(currentRegistryProcessors);
			// 执行
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			// 清理
			currentRegistryProcessors.clear();

			// Finally, invoke all other BeanDefinitionRegistryPostProcessors until no further ones appear.
			boolean reiterate = true;
			while (reiterate) {
				reiterate = false;
				// <1.4> 获取底层 BeanFactory 容器中所有 BeanDefinitionRegistryPostProcessor 类型的 Bean 们，遍历进行处理
				postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
				for (String ppName : postProcessorNames) {
					// 如果该 BeanDefinitionRegistryPostProcessors 在上述过程中没有执行过，则获取到对应的 Bean
					if (!processedBeans.contains(ppName)) {
						// 初始化
						currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
						processedBeans.add(ppName);
						reiterate = true;
					}
				}
				// 排序
				sortPostProcessors(currentRegistryProcessors, beanFactory);
				// 临时保存起来
				registryProcessors.addAll(currentRegistryProcessors);
				// 执行
				invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
				// 清理
				currentRegistryProcessors.clear();
			}
			/*
			 * <1.5> 上述执行完当前 Spring 应用上下文和底层 BeanFactory 容器中所有 BeanDefinitionRegistryPostProcessor 处理器中的
			 *  postProcessBeanDefinitionRegistry(registry) 方法后，
			 * 接下来执行它们的 postProcessBeanFactory(beanFactory) 方法
			 *
			 * 注意：BeanDefinitionRegistryPostProcessor 继承 BeanFactoryPostProcessor 接口
			 */
			// Now, invoke the postProcessBeanFactory callback of all processors handled so far.
			invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
			/*
			 * <1.6> 这里我们执行当前 Spring 应用上下文中 BeanFactoryPostProcessor 处理器（非 BeanDefinitionRegistryPostProcessors 类型）的
			 * postProcessBeanFactory(beanFactory) 方法
			 *
			 * 例如：PropertyPlaceholderConfigurer、PropertySourcesPlaceholderConfigurer
			 */
			invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
		}
		// <2> 执行当前 Spring 应用上下文中的 BeanFactoryPostProcessor 处理器的 postProcessBeanFactory(beanFactory) 方法
		else {
			// Invoke factory processors registered with the context instance.
			invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
		}

		// Do not initialize FactoryBeans here: We need to leave all regular beans
		// uninitialized to let the bean factory post-processors apply to them!
		// <3> 获取底层 BeanFactory 容器中所有 BeanFactoryPostProcessor 类型的 Bean 们
		String[] postProcessorNames =
				beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

		// Separate between BeanFactoryPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.
		List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		List<String> orderedPostProcessorNames = new ArrayList<>();
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		for (String ppName : postProcessorNames) {
			if (processedBeans.contains(ppName)) {
				// 上面已经执行过了则跳过
				// skip - already processed in first phase above
			}
			else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
			}
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// First, invoke the BeanFactoryPostProcessors that implement PriorityOrdered.
		// <3.1> PriorityOrdered 类型的 BeanFactoryPostProcessor 对象
		// 排序
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		// 执行
		invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

		// Next, invoke the BeanFactoryPostProcessors that implement Ordered.
		// <3.2> Ordered 类型的 BeanFactoryPostProcessor 对象
		List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
		for (String postProcessorName : orderedPostProcessorNames) {
			orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		// 排序
		sortPostProcessors(orderedPostProcessors, beanFactory);
		// 执行
		invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

		// Finally, invoke all other BeanFactoryPostProcessors.
		// <3.2> nonOrdered 的 BeanFactoryPostProcessor 对象
		List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
		for (String postProcessorName : nonOrderedPostProcessorNames) {
			nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		// 无需排序，直接执行
		invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

		// Clear cached merged bean definitions since the post-processors might have
		// modified the original metadata, e.g. replacing placeholders in values...
		beanFactory.clearMetadataCache();
	}
	private static void invokeBeanFactoryPostProcessors(
			Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) throws Exception {

		for (BeanFactoryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanFactory(beanFactory);
		}
	}
	/**
	 * Invoke the given BeanDefinitionRegistryPostProcessor beans.
	 */
	private static void invokeBeanDefinitionRegistryPostProcessors(
			Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) throws Exception {

		for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanDefinitionRegistry(registry);
		}
	}
	private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
		// Nothing to sort?
		if (postProcessors.size() <= 1) {
			return;
		}
		Comparator<Object> comparatorToUse = null;
		if (beanFactory instanceof DefaultListableBeanFactory) {
			comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
		}
		if (comparatorToUse == null) {
			comparatorToUse = OrderComparator.INSTANCE;
		}
		postProcessors.sort(comparatorToUse);
	}
	public static void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) throws Exception {

        // <1> 获取所有的 BeanPostProcessor 类型的 beanName
        // 这些 beanName 都已经全部加载到容器中去，但是没有实例化
        String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);

        // Register BeanPostProcessorChecker that logs an info message when
        // a bean is created during BeanPostProcessor instantiation, i.e. when
        // a bean is not eligible for getting processed by all BeanPostProcessors.
        // <2> 记录所有的 BeanPostProcessor 数量，为什么加 1 ？因为下面又添加了一个
        int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
        // 注册 BeanPostProcessorChecker，它主要是用于在 BeanPostProcessor 实例化期间记录日志
        // 当 Spring 中高配置的后置处理器还没有注册就已经开始了 bean 的实例化过程，这个时候便会打印 BeanPostProcessorChecker 中的内容
        beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));

        // Separate between BeanPostProcessors that implement PriorityOrdered, Ordered, and the rest.
        // <3> 开始注册 BeanPostProcessor
        // 实现了 `PriorityOrdered` 接口的 BeanPostProcessor 对应的 Bean 集合
        List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
        // MergedBeanDefinitionPostProcessor 类型对应的 Bean 集合
        List<BeanPostProcessor> internalPostProcessors = new ArrayList<>();
        // 实现了 `Ordered` 接口的 BeanPostProcessor 对应的 beanName 集合
        List<String> orderedPostProcessorNames = new ArrayList<>();
        // 没有顺序的 BeanPostProcessor 对应的 beanName 集合
        List<String> nonOrderedPostProcessorNames = new ArrayList<>();
        for (String ppName : postProcessorNames) {
            // PriorityOrdered
            if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                // 调用 getBean(...) 方法获取该 BeanPostProcessor 处理器的 Bean 对象
                BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
                priorityOrderedPostProcessors.add(pp);
                if (pp instanceof MergedBeanDefinitionPostProcessor) {
                    internalPostProcessors.add(pp);
                }
            }
            // Ordered
            else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
                orderedPostProcessorNames.add(ppName);
            }
            // 无序
            else {
                nonOrderedPostProcessorNames.add(ppName);
            }
        }

        // First, register the BeanPostProcessors that implement PriorityOrdered.
        // 第一步，对所有实现了 PriorityOrdered 的 BeanPostProcessor 进行排序
        sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
        // 进行注册，也就是添加至 DefaultListableBeanFactory 中
        registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);

        // Next, register the BeanPostProcessors that implement Ordered.
        // 第二步，获取所有实现了 Ordered 接口的 BeanPostProcessor 对应的 Bean 们
        List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>();
        for (String ppName : orderedPostProcessorNames) {
            // 调用 getBean(...) 方法获取该 BeanPostProcessor 处理器的 Bean 对象
            BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
            orderedPostProcessors.add(pp);
            if (pp instanceof MergedBeanDefinitionPostProcessor) {
                internalPostProcessors.add(pp);
            }
        }
        // 对所有实现了 Ordered 的 BeanPostProcessor 进行排序
        sortPostProcessors(orderedPostProcessors, beanFactory);
        // 进行注册，也就是添加至 DefaultListableBeanFactory 中
        registerBeanPostProcessors(beanFactory, orderedPostProcessors);

        // Now, register all regular BeanPostProcessors.
        // 第三步注册所有无序的 BeanPostProcessor
        List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
        for (String ppName : nonOrderedPostProcessorNames) {
            // 调用 getBean(...) 方法获取该 BeanPostProcessor 处理器的 Bean 对象
            BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
            nonOrderedPostProcessors.add(pp);
            if (pp instanceof MergedBeanDefinitionPostProcessor) {
                internalPostProcessors.add(pp);
            }
        }
        // 注册，无需排序
        registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);

        // Finally, re-register all internal BeanPostProcessors.
        // 最后，注册所有的 MergedBeanDefinitionPostProcessor 类型的 Bean 们
        sortPostProcessors(internalPostProcessors, beanFactory);
        registerBeanPostProcessors(beanFactory, internalPostProcessors);

        // Re-register post-processor for detecting inner beans as ApplicationListeners,
        // moving it to the end of the processor chain (for picking up proxies etc).
        // 重新注册 ApplicationListenerDetector（探测器），用于探测内部 ApplicationListener 类型的 Bean
        // 在完全初始化 Bean 后，如果是 ApplicationListener 类型且为单例模式，则添加到 Spring 应用上下文
        beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
    }
	/**
	 * Register the given BeanPostProcessor beans.
	 */
	private static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> postProcessors) {

		for (BeanPostProcessor postProcessor : postProcessors) {
			beanFactory.addBeanPostProcessor(postProcessor);
		}
	}
	/**
	 * BeanPostProcessor that logs an info message when a bean is created during
	 * BeanPostProcessor instantiation, i.e. when a bean is not eligible for
	 * getting processed by all BeanPostProcessors.
	 */
	private static final class BeanPostProcessorChecker implements BeanPostProcessor {

		private static final Log logger = LogFactory.getLog(BeanPostProcessorChecker.class);

		private final ConfigurableListableBeanFactory beanFactory;

		private final int beanPostProcessorTargetCount;

		public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, int beanPostProcessorTargetCount) {
			this.beanFactory = beanFactory;
			this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
		}

		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) {
			return bean;
		}

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
			if (!(bean instanceof BeanPostProcessor) && !isInfrastructureBean(beanName) &&
					this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount) {
				if (logger.isInfoEnabled()) {
					logger.info("Bean '" + beanName + "' of type [" + bean.getClass().getName() +
							"] is not eligible for getting processed by all BeanPostProcessors " +
							"(for example: not eligible for auto-proxying)");
				}
			}
			return bean;
		}

		private boolean isInfrastructureBean(@Nullable String beanName) throws Exception {
			if (beanName != null && this.beanFactory.containsBeanDefinition(beanName)) {
				BeanDefinition bd = this.beanFactory.getBeanDefinition(beanName);
				return (bd.getRole() == RootBeanDefinition.ROLE_INFRASTRUCTURE);
			}
			return false;
		}
	}

}
