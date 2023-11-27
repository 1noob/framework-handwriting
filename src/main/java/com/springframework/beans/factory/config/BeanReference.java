package com.springframework.beans.factory.config;

import com.springframework.beans.BeanMetadataElement;

public interface BeanReference extends BeanMetadataElement {

    /**
     * Return the target bean name that this reference points to (never {@code null}).
     */
    String getBeanName();

}
