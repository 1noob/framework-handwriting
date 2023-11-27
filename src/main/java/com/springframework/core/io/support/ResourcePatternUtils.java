package com.springframework.core.io.support;

import com.springframework.core.io.ResourceLoader;
import com.springframework.util.ResourceUtils;
import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class ResourcePatternUtils {


    public static boolean isUrl(@Nullable String resourceLocation) {
        return (resourceLocation != null &&
                (resourceLocation.startsWith(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX) ||
                        ResourceUtils.isUrl(resourceLocation)));
    }


    public static ResourcePatternResolver getResourcePatternResolver(@Nullable ResourceLoader resourceLoader) {
        if (resourceLoader instanceof ResourcePatternResolver) {
            return (ResourcePatternResolver) resourceLoader;
        }
        else if (resourceLoader != null) {
            return new PathMatchingResourcePatternResolver(resourceLoader);
        }
        else {
            return new PathMatchingResourcePatternResolver();
        }
    }

}

