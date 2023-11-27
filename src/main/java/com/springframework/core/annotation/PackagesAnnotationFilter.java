package com.springframework.core.annotation;

import com.springframework.util.Assert;

import java.util.Arrays;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
final class PackagesAnnotationFilter implements AnnotationFilter {

    private final String[] prefixes;

    private final int hashCode;

    PackagesAnnotationFilter(String... packages) {
        Assert.notNull(packages, "Packages array must not be null");
        this.prefixes = new String[packages.length];
        for (int i = 0; i < packages.length; i++) {
            String pkg = packages[i];
            Assert.hasText(pkg, "Packages array must not have empty elements");
            this.prefixes[i] = pkg + ".";
        }
        Arrays.sort(this.prefixes);
        this.hashCode = Arrays.hashCode(this.prefixes);
    }

    @Override
    public boolean matches(String typeName) {
        return false;
    }
}
