package dk.gtz.graphedit.model.lsp;

/**
 * Notification class containing a severity level and a message.
 * 
 * @param level   The severity level of the notication
 * @param message The message of the notication.
 */
public record ModelNotification(
		ModelNotificationLevel level,
		String message) {
}
