package com.springframework.context.event;

import com.springframework.context.ApplicationEvent;
import com.springframework.context.ApplicationListener;
import com.springframework.core.ResolvableType;
import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface ApplicationEventMulticaster {

    /**
     * Add a listener to be notified of all events.
     *
     * @param listener the listener to add
     */
    void addApplicationListener(ApplicationListener<?> listener);

    /**
     * Add a listener bean to be notified of all events.
     *
     * @param listenerBeanName the name of the listener bean to add
     */
    void addApplicationListenerBean(String listenerBeanName);

    /**
     * Remove a listener from the notification list.
     *
     * @param listener the listener to remove
     */
    void removeApplicationListener(ApplicationListener<?> listener);

    /**
     * Remove a listener bean from the notification list.
     *
     * @param listenerBeanName the name of the listener bean to remove
     */
    void removeApplicationListenerBean(String listenerBeanName);

    /**
     * Remove all listeners registered with this multicaster.
     * <p>After a remove call, the multicaster will perform no action
     * on event notification until new listeners are registered.
     */
    void removeAllListeners();

    /**
     * Multicast the given application event to appropriate listeners.
     * <p>Consider using {@link #multicastEvent(ApplicationEvent, ResolvableType)}
     * if possible as it provides better support for generics-based events.
     *
     * @param event the event to multicast
     */
    void multicastEvent(ApplicationEvent event);

    /**
     * Multicast the given application event to appropriate listeners.
     * <p>If the {@code eventType} is {@code null}, a default type is built
     * based on the {@code event} instance.
     *
     * @param event     the event to multicast
     * @param eventType the type of event (can be {@code null})
     * @since 4.2
     */
    void multicastEvent(ApplicationEvent event, @Nullable ResolvableType eventType);

}
