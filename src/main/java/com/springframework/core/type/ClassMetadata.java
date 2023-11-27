package com.springframework.core.type;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface ClassMetadata {
    boolean isInterface();
    String getClassName();
    boolean isIndependent();
    default boolean isConcrete() {
        return !(isInterface() || isAbstract());
    }
    boolean isAbstract();
}
