package com.springframework.beans.factory.xml;

/**
 *
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface NamespaceHandlerResolver {
    NamespaceHandler resolve(String namespaceUri);
}
