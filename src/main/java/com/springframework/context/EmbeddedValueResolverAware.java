package com.springframework.context;

import com.springframework.beans.factory.Aware;
import com.springframework.util.StringValueResolver;

public interface EmbeddedValueResolverAware extends Aware {

    /**
     * Set the StringValueResolver to use for resolving embedded definition values.
     */
    void setEmbeddedValueResolver(StringValueResolver resolver);

}
