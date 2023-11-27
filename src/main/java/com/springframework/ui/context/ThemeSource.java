package com.springframework.ui.context;

import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface ThemeSource {
    @Nullable
    Theme getTheme(String themeName);
}
