package com.springframework.beans.factory.parsing;

import com.springframework.util.StringUtils;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class QualifierEntry implements ParseState.Entry {

    private String typeName;


    public QualifierEntry(String typeName) {
        if (!StringUtils.hasText(typeName)) {
            throw new IllegalArgumentException("Invalid qualifier type '" + typeName + "'.");
        }
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "Qualifier '" + this.typeName + "'";
    }

}
