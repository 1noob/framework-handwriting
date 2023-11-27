package com.springframework.context.support;

import com.springframework.context.HierarchicalMessageSource;
import com.springframework.context.MessageSource;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class DelegatingMessageSource extends MessageSourceSupport implements HierarchicalMessageSource {
    @Override
    public void setParentMessageSource(MessageSource parent) {

    }

    @Override
    public MessageSource getParentMessageSource() {
        return null;
    }
}
