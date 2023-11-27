package com.springframework.beans;

import com.sun.istack.internal.Nullable;

import java.util.Map;

public interface PropertyAccessor {
    /**
     * Path separator for nested properties.
     * Follows normal Java conventions: getFoo().getBar() would be "foo.bar".
     */
    String NESTED_PROPERTY_SEPARATOR = ".";

    /**
     * Path separator for nested properties.
     * Follows normal Java conventions: getFoo().getBar() would be "foo.bar".
     */
    char NESTED_PROPERTY_SEPARATOR_CHAR = '.';

    /**
     * Marker that indicates the start of a property key for an
     * indexed or mapped property like "person.addresses[0]".
     */
    String PROPERTY_KEY_PREFIX = "[";

    /**
     * Marker that indicates the start of a property key for an
     * indexed or mapped property like "person.addresses[0]".
     */
    char PROPERTY_KEY_PREFIX_CHAR = '[';

    /**
     * Marker that indicates the end of a property key for an
     * indexed or mapped property like "person.addresses[0]".
     */
    String PROPERTY_KEY_SUFFIX = "]";

    /**
     * Marker that indicates the end of a property key for an
     * indexed or mapped property like "person.addresses[0]".
     */
    char PROPERTY_KEY_SUFFIX_CHAR = ']';


    /**
     * Determine whether the specified property is readable.
     * <p>Returns {@code false} if the property doesn't exist.
     *
     * @param propertyName the property to check
     *                     (may be a nested path and/or an indexed/mapped property)
     * @return whether the property is readable
     */
    boolean isReadableProperty(String propertyName);

    /**
     * Determine whether the specified property is writable.
     * <p>Returns {@code false} if the property doesn't exist.
     *
     * @param propertyName the property to check
     *                     (may be a nested path and/or an indexed/mapped property)
     * @return whether the property is writable
     */
    boolean isWritableProperty(String propertyName);


    @Nullable
    Class<?> getPropertyType(String propertyName) throws RuntimeException;


    @Nullable
    Object getPropertyValue(String propertyName) throws RuntimeException;


    void setPropertyValue(String propertyName, @Nullable Object value) throws RuntimeException;


    void setPropertyValue(PropertyValue pv) throws RuntimeException;


    void setPropertyValues(Map<?, ?> map) throws RuntimeException;


    void setPropertyValues(PropertyValues pvs) throws RuntimeException;

    void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown)
            throws RuntimeException;


    void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown, boolean ignoreInvalid)
            throws RuntimeException;

}
