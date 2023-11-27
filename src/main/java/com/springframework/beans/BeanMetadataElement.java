package com.springframework.beans;

/**
 * 用于获取定义 Bean 的源对象，在实现类中通过 Object 对象保存，所谓的源对象就是定义这个 Bean 的资源
 * （XML 标签对象或者 .class 文件资源对象）
 * @author Gary
 */
public interface BeanMetadataElement {
    default Object getSource() {
        return null;
    }
}
