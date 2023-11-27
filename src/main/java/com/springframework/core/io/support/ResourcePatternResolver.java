package com.springframework.core.io.support;

import com.springframework.core.io.Resource;
import com.springframework.core.io.ResourceLoader;

import java.io.IOException;

/**
 * @author Gary
 */
public interface ResourcePatternResolver extends ResourceLoader {
    String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

    Resource[] getResources(String locationPattern) throws IOException;

}
