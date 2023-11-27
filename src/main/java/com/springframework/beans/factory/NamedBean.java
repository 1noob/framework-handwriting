package com.springframework.beans.factory;

public interface NamedBean {

    /**
     * Return the name of this bean in a Spring bean factory, if known.
     */
    String getBeanName();

}
