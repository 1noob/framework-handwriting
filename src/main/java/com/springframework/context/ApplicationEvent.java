package com.springframework.context;

import java.util.EventObject;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class ApplicationEvent extends EventObject {


    /**
     * use serialVersionUID from Spring 1.2 for interoperability.
     */
    private static final long serialVersionUID = 7099057708183571937L;

    /**
     * System time when the event happened.
     */
    private final long timestamp;


    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public ApplicationEvent(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }


    /**
     * Return the system time in milliseconds when the event occurred.
     */
    public final long getTimestamp() {
        return this.timestamp;
    }
}
