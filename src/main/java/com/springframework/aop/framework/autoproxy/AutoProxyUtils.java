package com.springframework.aop.framework.autoproxy;

import com.springframework.core.Conventions;
import com.springframework.util.Assert;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class AutoProxyUtils {
    public static final String PRESERVE_TARGET_CLASS_ATTRIBUTE =
            Conventions.getQualifiedAttributeName(AutoProxyUtils.class, "preserveTargetClass");


}
