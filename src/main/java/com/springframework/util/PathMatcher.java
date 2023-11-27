package com.springframework.util;

/**
 * @author Gary
 */
public interface PathMatcher {
    boolean isPattern(String path);

    boolean match(String pattern, String path);

    boolean matchStart(String pattern, String path);

}
