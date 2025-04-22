package dk.gtz.graphedit.model.lsp;

/**
 * A language server progress report.
 * This is used to indicate what the server is currently doing.
 * 
 * @param token   Progress report identifier
 * @param type    The type of the progress report
 * @param title   Progress report title
 * @param message The message of the progress report
 */
public record ModelLanguageServerProgress(
		String token,
		ModelLanguageServerProgressType type,
		String title,
		String message) {
}
