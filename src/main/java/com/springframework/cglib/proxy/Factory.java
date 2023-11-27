package com.springframework.cglib.proxy;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface Factory {
    Object newInstance(Callback var1);

    Object newInstance(Callback[] var1);

    Object newInstance(Class[] var1, Object[] var2, Callback[] var3);

    Callback getCallback(int var1);

    void setCallback(int var1, Callback var2);

    void setCallbacks(Callback[] var1);

    Callback[] getCallbacks();
}
