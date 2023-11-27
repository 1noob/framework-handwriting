package com.springframework.beans;



import com.springframework.core.MethodParameter;
import com.springframework.core.ResolvableType;
import com.springframework.core.convert.TypeDescriptor;
import com.springframework.util.ReflectionUtils;
import com.sun.istack.internal.Nullable;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;

/**
 * @Author 虎哥
 * @Description //TODO
 * 要带着问题去学习,多猜想多验证
 **/
public class BeanWrapperImpl extends AbstractNestablePropertyAccessor implements BeanWrapper {
    public BeanWrapperImpl(Object object) {
        super(object);
    }
    @Override
    protected BeanWrapperImpl newNestedPropertyAccessor(Object object, String nestedPath) {
        return new BeanWrapperImpl(object, nestedPath, this);
    }
    private BeanWrapperImpl(Object object, String nestedPath, BeanWrapperImpl parent) {
        super(object, nestedPath, parent);
        this.setSecurityContext(parent.acc);
    }
    @Override
    public void setWrappedInstance(Object object, @Nullable String nestedPath, @Nullable Object rootObject) {
        super.setWrappedInstance(object, nestedPath, rootObject);
//        this.setIntrospectionClass(this.getWrappedClass());
    }
    public void setSecurityContext(@Nullable AccessControlContext acc) {
        this.acc = acc;
    }
    //    protected void setIntrospectionClass(Class<?> clazz) {
//        if (this.cachedIntrospectionResults != null && this.cachedIntrospectionResults.getBeanClass() != clazz) {
//            this.cachedIntrospectionResults = null;
//        }
//
//    }
    @Nullable
    private CachedIntrospectionResults cachedIntrospectionResults;
    @Override
    @Nullable
    protected BeanWrapperImpl.BeanPropertyHandler getLocalPropertyHandler(String propertyName) throws CloneNotSupportedException {
        PropertyDescriptor pd = this.getCachedIntrospectionResults().getPropertyDescriptor(propertyName);
        return pd != null ? new BeanWrapperImpl.BeanPropertyHandler(pd) : null;
    }
    private CachedIntrospectionResults getCachedIntrospectionResults() {
        if (this.cachedIntrospectionResults == null) {
            this.cachedIntrospectionResults = CachedIntrospectionResults.forClass(this.getWrappedClass());
        }
        return this.cachedIntrospectionResults;
    }
    @Override
    protected Exception createNotWritablePropertyException(String propertyName) {
        throw new RuntimeException(propertyName);
    }
    @Override
    public void setPropertyValue2(String propertyName, Object value) throws RuntimeException {

    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        return new PropertyDescriptor[0];
    }

    @Override
    public PropertyDescriptor getPropertyDescriptor(String propertyName)  {
        return null;
    }

    @Override
    public void setAutoGrowNestedPaths(boolean autoGrowNestedPaths) {

    }

    @Override
    public void setPropertyValues2(PropertyValues var1)  {
        setPropertyValues(var1);
    }

    @Override
    public boolean isReadableProperty(String propertyName) {
        return false;
    }

    @Override
    public boolean isWritableProperty(String propertyName) {
        return false;
    }

    @Override
    public Class<?> getPropertyType(String propertyName) throws RuntimeException {
        return null;
    }

    @Override
    public <T> T convertIfNecessary(Object value, Class<T> requiredType) throws RuntimeException {
        return null;
    }

    @Override
    public <T> T convertIfNecessary(Object value, Class<T> requiredType, MethodParameter methodParam) throws RuntimeException {
        return null;
    }

    @Override
    public TypeDescriptor getPropertyTypeDescriptor(String propertyName)  {
        return null;
    }

    @Override
    public Object getPropertyValue(String propertyName) {
        return null;
    }

    @Override
    public void setPropertyValue(String propertyName, Object value)  {

    }

    @Override
    public void setPropertyValues(Map<?, ?> map) throws RuntimeException {

    }

    @Override
    public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown) throws RuntimeException {

    }


    private class BeanPropertyHandler extends AbstractNestablePropertyAccessor.PropertyHandler {
        private final PropertyDescriptor pd;

        public BeanPropertyHandler(PropertyDescriptor pd) {
            super(pd.getPropertyType(), pd.getReadMethod() != null, pd.getWriteMethod() != null);
            this.pd = pd;
        }

        @Override
        public TypeDescriptor toTypeDescriptor() {
            return null;
        }

        @Override
        public ResolvableType getResolvableType() {
            return ResolvableType.forMethodReturnType(this.pd.getReadMethod());
        }

        @Override
        public TypeDescriptor nested(int var1) {
            return null;
        }


        @Override
        @Nullable
        public Object getValue() throws Exception {
            Method readMethod = this.pd.getReadMethod();
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>) () -> {
                    ReflectionUtils.makeAccessible(readMethod);
                    return null;
                });

                try {
                    return AccessController.doPrivileged((PrivilegedExceptionAction<Object>) () -> {
                        return readMethod.invoke(BeanWrapperImpl.this.getWrappedInstance(), (Object[]) null);
                    }, BeanWrapperImpl.this.acc);
                } catch (PrivilegedActionException var3) {
                    throw var3.getException();
                }
            } else {
                ReflectionUtils.makeAccessible(readMethod);
                return readMethod.invoke(BeanWrapperImpl.this.getWrappedInstance(), (Object[]) null);
            }
        }

        @Override
        public void setValue(@Nullable Object value) throws Exception {
            Method writeMethod = this.pd.getWriteMethod();
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>) () -> {
                    ReflectionUtils.makeAccessible(writeMethod);
                    return null;
                });

                try {
                    AccessController.doPrivileged((PrivilegedExceptionAction<Object>) () -> {
                        return writeMethod.invoke(BeanWrapperImpl.this.getWrappedInstance(), value);
                    }, BeanWrapperImpl.this.acc);
                } catch (PrivilegedActionException var4) {
                    throw var4.getException();
                }
            } else {
                ReflectionUtils.makeAccessible(writeMethod);
                writeMethod.invoke(BeanWrapperImpl.this.getWrappedInstance(), value);
            }

        }

        @Nullable
        private AccessControlContext acc;
        @Nullable
        CachedIntrospectionResults cachedIntrospectionResults;

    }
    @Nullable
    private AccessControlContext acc;


}
