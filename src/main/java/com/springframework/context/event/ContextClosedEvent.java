package com.springframework.context.event;

import com.springframework.context.ApplicationContext;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class ContextClosedEvent extends ApplicationContextEvent {

    /**
     * Creates a new ContextClosedEvent.
     *
     * @param source the {@code ApplicationContext} that has been closed
     *               (must not be {@code null})
     */
    public ContextClosedEvent(ApplicationContext source) {
        super(source);
    }
}

