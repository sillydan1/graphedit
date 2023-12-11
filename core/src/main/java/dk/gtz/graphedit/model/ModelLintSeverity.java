package dk.gtz.graphedit.model;

/**
 * Severity level enumeration for lints.
 */
public enum ModelLintSeverity {
    /**
     * Hint-level lints are meant for small notifications about things that could be optimized.
     */
    HINT,

    /**
     * Info-level lints are meant to indicate general non-problematic information.
     */
    INFO,

    /**
     * Warning-level lints are meant to indicate if something can potentially go wrong or if something is non-critically erronious.
     */
    WARNING,

    /**
     * Error-level lints are meant to indicate when something is erronious and should be fixed.
     */
    ERROR
}
