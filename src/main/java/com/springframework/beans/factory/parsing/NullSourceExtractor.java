package com.springframework.beans.factory.parsing;

import com.springframework.core.io.Resource;
import com.sun.istack.internal.Nullable;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class NullSourceExtractor implements SourceExtractor {
    /**
     * This implementation simply returns {@code null} for any input.
     */
    @Override
    @Nullable
    public Object extractSource(Object sourceCandidate, @Nullable Resource definitionResource) {
        return null;
    }

}
