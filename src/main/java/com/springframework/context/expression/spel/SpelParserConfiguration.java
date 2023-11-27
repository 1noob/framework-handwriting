package com.springframework.context.expression.spel;

import com.springframework.core.SpringProperties;
import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class SpelParserConfiguration {
    public SpelParserConfiguration(@Nullable SpelCompilerMode compilerMode, @Nullable ClassLoader compilerClassLoader, int maximumAutoGrowSize, ClassLoader compilerClassLoader1) {
        this(compilerMode, compilerClassLoader, false, false, 2147483647);
    }

    private static final SpelCompilerMode defaultCompilerMode;
    private final SpelCompilerMode compilerMode;
    private final ClassLoader compilerClassLoader;
    private final boolean autoGrowNullReferences;
    private final boolean autoGrowCollections;
    private final int maximumAutoGrowSize;

    public SpelParserConfiguration(@Nullable SpelCompilerMode compilerMode, @Nullable ClassLoader compilerClassLoader, boolean autoGrowNullReferences, boolean autoGrowCollections, int maximumAutoGrowSize) {
        this.compilerMode = compilerMode != null ? compilerMode : defaultCompilerMode;
        this.compilerClassLoader = compilerClassLoader;
        this.autoGrowNullReferences = autoGrowNullReferences;
        this.autoGrowCollections = autoGrowCollections;
        this.maximumAutoGrowSize = maximumAutoGrowSize;
    }

    static {
        String compilerMode = SpringProperties.getProperty("spring.expression.compiler.mode");
        defaultCompilerMode = compilerMode != null ? SpelCompilerMode.valueOf(compilerMode.toUpperCase()) : SpelCompilerMode.OFF;
    }


}
