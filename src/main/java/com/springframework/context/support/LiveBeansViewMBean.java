package com.springframework.context.support;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface LiveBeansViewMBean {

    /**
     * Generate a JSON snapshot of current beans and their dependencies.
     */
    String getSnapshotAsJson();

}
