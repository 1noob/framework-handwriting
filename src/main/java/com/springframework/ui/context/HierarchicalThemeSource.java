package com.springframework.ui.context;

import com.sun.istack.internal.Nullable;

public interface HierarchicalThemeSource extends ThemeSource {

    /**
     * Set the parent that will be used to try to resolve theme messages
     * that this object can't resolve.
     * @param parent the parent ThemeSource that will be used to
     * resolve messages that this object can't resolve.
     * May be {@code null}, in which case no further resolution is possible.
     */
    void setParentThemeSource(@Nullable ThemeSource parent);

    /**
     * Return the parent of this ThemeSource, or {@code null} if none.
     */
    @Nullable
    ThemeSource getParentThemeSource();

}
