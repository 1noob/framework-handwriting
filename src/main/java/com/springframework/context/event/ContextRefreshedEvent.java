package com.springframework.context.event;

import com.springframework.context.ApplicationContext;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class ContextRefreshedEvent extends ApplicationContextEvent {

    /**
     * Create a new ContextRefreshedEvent.
     * @param source the {@code ApplicationContext} that has been initialized
     * or refreshed (must not be {@code null})
     */
    public ContextRefreshedEvent(ApplicationContext source) {
        super(source);
    }

}
