package dk.gtz.graphedit.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import javafx.application.Platform;

public class EditorLogAppender extends AppenderBase<ILoggingEvent> {
    private static record LogConsumer(Level level, Consumer<String> consumer) {}
    private static Map<UUID, LogConsumer> subscribers = new HashMap<>();

    @Override
    protected void append(ILoggingEvent eventObject) { }

    @Override
    public synchronized void doAppend(ILoggingEvent eventObject) {
	var messageCopy = eventObject.getFormattedMessage();
	subscribers.entrySet().stream()
	    .filter(e -> e.getValue().level() == eventObject.getLevel())
	    .forEach(r -> Platform.runLater(() -> r.getValue().consumer().accept(messageCopy)));
    }

    /**
     * Add a logconsume function to the list of consumers
     * @param levelFilter the log level at which the logconsumer function will be invoked
     * @param logConsumer the logconsumer function to invoke
     * @return the registered key. Use this to unsubscribe again later if needed
     */
    public static UUID subscribe(Level levelFilter, Consumer<String> logConsumer) {
	var key = UUID.randomUUID();
	subscribers.put(key, new LogConsumer(levelFilter, logConsumer));
	return key;
    }

    /**
     * Will remove the logconsume function with the associated key.
     * @param key the identifying key of the logconsumer to remove
     */
    public static void unsubscribe(UUID key) {
	if(subscribers.containsKey(key))
	    subscribers.remove(key);
    }
}

