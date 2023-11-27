package com.springframework.beans.factory.parsing;

import com.springframework.core.io.Resource;
import com.springframework.util.Assert;
import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class Location {

    private final Resource resource;

    @Nullable
    private final Object source;


    /**
     * Create a new instance of the {@link Location} class.
     * @param resource the resource with which this location is associated
     */
    public Location(Resource resource) {
        this(resource, null);
    }

    /**
     * Create a new instance of the {@link Location} class.
     * @param resource the resource with which this location is associated
     * @param source the actual location within the associated resource
     * (may be {@code null})
     */
    public Location(Resource resource, @Nullable Object source) {
        Assert.notNull(resource, "Resource must not be null");
        this.resource = resource;
        this.source = source;
    }


    /**
     * Get the resource with which this location is associated.
     */
    public Resource getResource() {
        return this.resource;
    }

    /**
     * Get the actual location within the associated {@link #getResource() resource}
     * (may be {@code null}).
     * <p>See the {@link Location class level javadoc for this class} for examples
     * of what the actual type of the returned object may be.
     */
    @Nullable
    public Object getSource() {
        return this.source;
    }

}
