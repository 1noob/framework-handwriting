package com.springframework.context;

import com.springframework.beans.factory.Aware;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface MessageSourceAware extends Aware {

    void setMessageSource(MessageSource messageSource);

}
