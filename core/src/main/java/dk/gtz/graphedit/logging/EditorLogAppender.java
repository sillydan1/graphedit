package dk.gtz.graphedit.logging;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import javafx.application.Platform;

public class EditorLogAppender extends AppenderBase<ILoggingEvent> {
    private static record LogConsumer(Level level, Consumer<String> consumer) {}
    private static List<LogConsumer> subscribers = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent eventObject) { }

    @Override
    public synchronized void doAppend(ILoggingEvent eventObject) {
	var messageCopy = eventObject.getMessage(); // TODO: This is not actually formatted yet. So strings will look like so: "hello '{}'" instead of "hello 'foo'"
	subscribers.stream()
	    .filter(e -> e.level() == eventObject.getLevel())
	    .forEach(r -> Platform.runLater(() -> r.consumer().accept(messageCopy)));
    }

    // TODO: How to unsubscribe again?
    public static void subscribe(Level levelFilter, Consumer<String> logConsumer) {
	subscribers.add(new LogConsumer(levelFilter, logConsumer));
    }
}

