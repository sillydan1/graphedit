package dk.gtz.graphedit.model.lsp;

import ch.qos.logback.classic.Level;

/**
 * Notification class containing a severity level and a message.
 * @param level The severity level of the notication, see {@link Level}.
 * @param message The message of the notication.
 */
public record ModelNotification(
                String level,
                String message) {}
