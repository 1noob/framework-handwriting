package com.springframework.core;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
abstract class GraalDetector {

    // See https://github.com/oracle/graal/blob/master/sdk/src/org.graalvm.nativeimage/src/org/graalvm/nativeimage/ImageInfo.java
    private static final boolean imageCode = (System.getProperty("org.graalvm.nativeimage.imagecode") != null);


    /**
     * Return whether this runtime environment lives within a native image.
     */
    public static boolean inImageCode() {
        return imageCode;
    }

}
