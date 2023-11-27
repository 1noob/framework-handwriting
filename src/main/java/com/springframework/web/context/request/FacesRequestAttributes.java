package com.springframework.web.context.request;

import com.springframework.util.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.context.FacesContext;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class FacesRequestAttributes  implements RequestAttributes {
    private static final Log logger = LogFactory.getLog(FacesRequestAttributes.class);

    private final FacesContext facesContext;
    public FacesRequestAttributes(FacesContext facesContext) {
        Assert.notNull(facesContext, "FacesContext must not be null");
        this.facesContext = facesContext;
    }

    @Override
    public Object getAttribute(String name, int scope) {
        return null;
    }

    @Override
    public void setAttribute(String name, Object value, int scope) {

    }
}
