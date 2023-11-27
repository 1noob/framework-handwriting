package com.springframework.core.annotation;

import java.util.List;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class AnnotationAwareOrderComparator extends OrderComparator {
    public static void sort(List<?> list) {
        if (list.size() > 1) {
            list.sort(INSTANCE);
        }
    }
    public static final AnnotationAwareOrderComparator INSTANCE = new AnnotationAwareOrderComparator();

}
