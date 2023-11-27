package com.springframework.cglib.core;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class ClassLoaderAwareGeneratorStrategy extends DefaultGeneratorStrategy {
    private final ClassLoader classLoader;

    public ClassLoaderAwareGeneratorStrategy(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public byte[] generate(ClassGenerator cg) throws Exception {
        if (this.classLoader == null) {
            return super.generate(cg);
        }

        Thread currentThread = Thread.currentThread();
        ClassLoader threadContextClassLoader;
        try {
            threadContextClassLoader = currentThread.getContextClassLoader();
        }
        catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
            return super.generate(cg);
        }

        boolean overrideClassLoader = !this.classLoader.equals(threadContextClassLoader);
        if (overrideClassLoader) {
            currentThread.setContextClassLoader(this.classLoader);
        }
        try {
            return super.generate(cg);
        }
        finally {
            if (overrideClassLoader) {
                // Reset original thread context ClassLoader.
                currentThread.setContextClassLoader(threadContextClassLoader);
            }
        }
    }

}
