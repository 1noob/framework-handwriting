package com.springframework.context.support;

import com.springframework.context.ApplicationContext;
import com.springframework.context.ApplicationContextAware;
import com.springframework.context.ConfigurableApplicationContext;
import com.sun.istack.internal.Nullable;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class LiveBeansView implements LiveBeansViewMBean, ApplicationContextAware {
    /**
     * The "MBean Domain" property name.
     */
    public static final String MBEAN_DOMAIN_PROPERTY_NAME = "spring.liveBeansView.mbeanDomain";

    /**
     * The MBean application key.
     */
    public static final String MBEAN_APPLICATION_KEY = "application";
    private static final Set<ConfigurableApplicationContext> applicationContexts = new LinkedHashSet<>();

    @Nullable
    private static String applicationName;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws Exception {

    }
    static void unregisterApplicationContext(ConfigurableApplicationContext applicationContext) {
        synchronized (applicationContexts) {
            if (applicationContexts.remove(applicationContext) && applicationContexts.isEmpty()) {
                try {
                    MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                    String mbeanDomain = applicationContext.getEnvironment().getProperty(MBEAN_DOMAIN_PROPERTY_NAME);
                    if (mbeanDomain != null) {
                        server.unregisterMBean(new ObjectName(mbeanDomain, MBEAN_APPLICATION_KEY, applicationName));
                    }
                }
                catch (Throwable ex) {
                    throw new RuntimeException("Failed to unregister LiveBeansView MBean", ex);
                }
                finally {
                    applicationName = null;
                }
            }
        }
    }
    @Override
    public String getSnapshotAsJson() {
        return null;
    }

    static void registerApplicationContext(ConfigurableApplicationContext applicationContext) {
        String mbeanDomain = applicationContext.getEnvironment().getProperty(MBEAN_DOMAIN_PROPERTY_NAME);
        if (mbeanDomain != null) {
            synchronized (applicationContexts) {
                if (applicationContexts.isEmpty()) {
                    try {
                        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                        applicationName = applicationContext.getApplicationName();
                        server.registerMBean(new LiveBeansView(),
                                new ObjectName(mbeanDomain, MBEAN_APPLICATION_KEY, applicationName));
                    }
                    catch (Throwable ex) {
                        throw new RuntimeException("Failed to register LiveBeansView MBean", ex);
                    }
                }
                applicationContexts.add(applicationContext);
            }
        }
    }

}
