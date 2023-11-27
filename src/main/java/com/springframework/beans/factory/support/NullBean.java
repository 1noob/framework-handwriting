package com.springframework.beans.factory.support;

/**
 * @Author 虎哥
 * @Description //TODO
 * 要带着问题去学习,多猜想多验证
 **/
final class NullBean {
    NullBean() {
    }


    @Override
    public boolean equals(Object obj) {
        return (this == obj || obj == null);
    }

    @Override
    public int hashCode() {
        return NullBean.class.hashCode();
    }

    @Override
    public String toString() {
        return "null";
    }

}
