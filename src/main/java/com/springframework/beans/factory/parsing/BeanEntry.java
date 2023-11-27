package com.springframework.beans.factory.parsing;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class BeanEntry implements ParseState.Entry {

    private String beanDefinitionName;


    /**
     * Creates a new instance of {@link BeanEntry} class.
     * @param beanDefinitionName the name of the associated bean definition
     */
    public BeanEntry(String beanDefinitionName) {
        this.beanDefinitionName = beanDefinitionName;
    }


    @Override
    public String toString() {
        return "Bean '" + this.beanDefinitionName + "'";
    }

}
