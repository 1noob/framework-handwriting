package com.springframework.beans;

import com.springframework.core.AttributeAccessorSupport;
import com.sun.istack.internal.Nullable;

/**
 * @author Gary
 */
public class BeanMetadataAttributeAccessor extends AttributeAccessorSupport implements BeanMetadataElement {
    @Nullable
    private Object source;


    /**
     * Set the configuration source {@code Object} for this metadata element.
     * <p>The exact type of the object will depend on the configuration mechanism used.
     */
    public void setSource(@Nullable Object source) {
        this.source = source;
    }

    @Override
    @Nullable
    public Object getSource() {
        return this.source;
    }


    /**
     * Add the given BeanMetadataAttribute to this accessor's set of attributes.
     *
     * @param attribute the BeanMetadataAttribute object to register
     */
    public void addMetadataAttribute(BeanMetadataAttribute attribute) {
        super.setAttribute(attribute.getName(), attribute);
    }

    /**
     * Look up the given BeanMetadataAttribute in this accessor's set of attributes.
     *
     * @param name the name of the attribute
     * @return the corresponding BeanMetadataAttribute object,
     * or {@code null} if no such attribute defined
     */
    @Nullable
    public BeanMetadataAttribute getMetadataAttribute(String name) {
        return (BeanMetadataAttribute) super.getAttribute(name);
    }

    @Override
    public void setAttribute(String name, @Nullable Object value) {
        super.setAttribute(name, new BeanMetadataAttribute(name, value));
    }

    @Override
    @Nullable
    public Object getAttribute(String name) {
        BeanMetadataAttribute attribute = (BeanMetadataAttribute) super.getAttribute(name);
        return (attribute != null ? attribute.getValue() : null);
    }

    @Override
    @Nullable
    public Object removeAttribute(String name) {
        BeanMetadataAttribute attribute = (BeanMetadataAttribute) super.removeAttribute(name);
        return (attribute != null ? attribute.getValue() : null);
    }
}
