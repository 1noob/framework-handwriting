package com.springframework.web.context.request;

import com.springframework.beans.factory.ObjectFactory;
import com.springframework.beans.factory.config.Scope;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class AbstractRequestAttributesScope implements Scope {
    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        Object scopedObject = attributes.getAttribute(name, getScope());
        if (scopedObject == null) {
            scopedObject = objectFactory.getObject();
            attributes.setAttribute(name, scopedObject, getScope());
            // Retrieve object again, registering it for implicit session attribute updates.
            // As a bonus, we also allow for potential decoration at the getAttribute level.
            Object retrievedObject = attributes.getAttribute(name, getScope());
            if (retrievedObject != null) {
                // Only proceed with retrieved object if still present (the expected case).
                // If it disappeared concurrently, we return our locally created instance.
                scopedObject = retrievedObject;
            }
        }
        return scopedObject;
    }


    protected abstract int getScope();
}
