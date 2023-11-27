package com.springframework.beans.factory.parsing;

import java.util.EventListener;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface ReaderEventListener extends EventListener {
    void componentRegistered(ComponentDefinition componentDefinition);

    void defaultsRegistered(DefaultsDefinition defaultsDefinition);

    void importProcessed(ImportDefinition importDefinition);
    void aliasRegistered(AliasDefinition aliasDefinition);

}
