package dk.gtz.graphedit.model.lsp;

/**
 * Enumeration of the type of language server progress reports.
 */
public enum ModelLanguageServerProgressType {
        /**
         * Indicates that a language server process has started.
         */
        BEGIN,

        /**
         * Indicates that a language server process has progressed, but is still running.
         */
        PROGRESS,

        /**
         * Indiccates that a language server process has finished successfully.
         */
        END,

        /**
         * Indicates that a language server process has finished errorniously.
         */
        END_FAIL
}
