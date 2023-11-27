package com.springframework.context.event;

import com.springframework.context.ApplicationContext;
import com.springframework.context.ApplicationEvent;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class ApplicationContextEvent extends ApplicationEvent {
    /**
     * Create a new ContextStartedEvent.
     *
     * @param source the {@code ApplicationContext} that the event is raised for
     *               (must not be {@code null})
     */
    public ApplicationContextEvent(ApplicationContext source) {
        super(source);
    }

    /**
     * Get the {@code ApplicationContext} that the event was raised for.
     */
    public final ApplicationContext getApplicationContext() {
        return (ApplicationContext) getSource();
    }

}
