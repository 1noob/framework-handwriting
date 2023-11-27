package com.springframework.web.context.request;

import com.sun.istack.internal.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class ServletWebRequest extends ServletRequestAttributes implements NativeWebRequest {
    public ServletWebRequest(HttpServletRequest request) {
        super(request);
    }
    public ServletWebRequest(HttpServletRequest request, @Nullable HttpServletResponse response) {
        super(request, response);
    }

}
