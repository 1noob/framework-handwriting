package com.springframework.beans.factory.parsing;

public interface ProblemReporter {

    /**
     * Called when a fatal error is encountered during the parsing process.
     * <p>Implementations must treat the given problem as fatal,
     * i.e. they have to eventually raise an exception.
     * @param problem the source of the error (never {@code null})
     */
    void fatal(Problem problem);

    /**
     * Called when an error is encountered during the parsing process.
     * <p>Implementations may choose to treat errors as fatal.
     * @param problem the source of the error (never {@code null})
     */
    void error(Problem problem);

    /**
     * Called when a warning is raised during the parsing process.
     * <p>Warnings are <strong>never</strong> considered to be fatal.
     * @param problem the source of the warning (never {@code null})
     */
    void warning(Problem problem);
}
