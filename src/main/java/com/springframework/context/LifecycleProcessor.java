package com.springframework.context;

/**
 * @author Gary
 */
public interface LifecycleProcessor extends Lifecycle {
    /**
     * Notification of context refresh, e.g. for auto-starting components.
     */
    void onRefresh();

    /**
     * Notification of context close phase, e.g. for auto-stopping components.
     */
    void onClose();
}
