package com.springframework.context;

import com.springframework.core.ResolvableType;
import com.springframework.core.ResolvableTypeProvider;
import com.springframework.util.Assert;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class PayloadApplicationEvent<T> extends ApplicationEvent implements ResolvableTypeProvider {
    private final T payload;


    /**
     * Create a new PayloadApplicationEvent.
     *
     * @param source  the object on which the event initially occurred (never {@code null})
     * @param payload the payload object (never {@code null})
     */
    public PayloadApplicationEvent(Object source, T payload) {
        super(source);
        Assert.notNull(payload, "Payload must not be null");
        this.payload = payload;
    }


    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(getPayload()));
    }

    /**
     * Return the payload of the event.
     */
    public T getPayload() {
        return this.payload;
    }

}
