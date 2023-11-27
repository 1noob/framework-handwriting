package com.springframework.beans.factory.parsing;

import com.springframework.core.io.Resource;
import com.sun.istack.internal.Nullable;

public class ReaderContext {
    private final Resource resource;

    private final ProblemReporter problemReporter;

    private final ReaderEventListener eventListener;

    private final SourceExtractor sourceExtractor;

    /**
     * Raise a regular error.
     */
    public void error(String message, @Nullable Object source) {
        error(message, source, null, null);
    }
    public void fireDefaultsRegistered(DefaultsDefinition defaultsDefinition) {
        this.eventListener.defaultsRegistered(defaultsDefinition);
    }

    public ReaderContext(Resource resource, ProblemReporter problemReporter,
                         ReaderEventListener eventListener, SourceExtractor sourceExtractor) {

        this.resource = resource;
        this.problemReporter = problemReporter;
        this.eventListener = eventListener;
        this.sourceExtractor = sourceExtractor;
    }
    public void fireAliasRegistered(String beanName, String alias, @Nullable Object source) {
        this.eventListener.aliasRegistered(new AliasDefinition(beanName, alias, source));
    }

    /**
     * Fire an import-processed event.
     */
    public void fireImportProcessed(String importedResource, Resource[] actualResources, @Nullable Object source) {
        this.eventListener.importProcessed(new ImportDefinition(importedResource, actualResources, source));
    }
    public Object extractSource(Object sourceCandidate) {
        return this.sourceExtractor.extractSource(sourceCandidate, this.resource);
    }

    public void fireComponentRegistered(ComponentDefinition componentDefinition) {
        this.eventListener.componentRegistered(componentDefinition);
    }

    /**
     * Raise a regular error.
     */
    public void error(String message, @Nullable Object source, @Nullable ParseState parseState) {
        error(message, source, parseState, null);
    }
    /**
     * Raise a regular error.
     */
    public void error(String message, @Nullable Object source, @Nullable Throwable cause) {
        error(message, source, null, cause);
    }
    /**
     * Raise a regular error.
     */
    public void error(String message, @Nullable Object source, @Nullable ParseState parseState, @Nullable Throwable cause) {
        Location location = new Location(getResource(), source);
        this.problemReporter.error(new Problem(message, location, parseState, cause));
    }
    public final Resource getResource() {
        return this.resource;
    }
}
