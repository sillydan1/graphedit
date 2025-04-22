package dk.gtz.graphedit.model.lsp;

/**
 * Enumeration for the severity level of a {@link ModelNotification}.
 */
public enum ModelNotificationLevel {
	/**
	 * Indicates that an unrecoverable error occurred.
	 */
	ERROR,
	/**
	 * Indicates that something potentially wrong occurred.
	 */
	WARNING,
	/**
	 * Indicates that a notification is informative and not critical.
	 */
	INFO,
	/**
	 * Used for development debugging. Should contain information relevant to
	 * developers.
	 */
	DEBUG,
	/**
	 * Highly verbose trace information, this can be useful for full understanding
	 * of what the system is doing.
	 */
	TRACE
}
