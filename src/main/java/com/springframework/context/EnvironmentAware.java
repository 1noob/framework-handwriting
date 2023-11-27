package com.springframework.context;

import com.springframework.beans.factory.Aware;
import com.springframework.core.env.Environment;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface EnvironmentAware extends Aware {

    /**
     * Set the {@code Environment} that this component runs in.
     */
    void setEnvironment(Environment environment);

}
