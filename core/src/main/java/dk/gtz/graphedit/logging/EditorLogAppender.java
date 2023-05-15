package dk.gtz.graphedit.logging;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import javafx.application.Platform;

public class EditorLogAppender extends AppenderBase<ILoggingEvent> {
    private static List<Consumer<String>> subscribers = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent eventObject) { }

    @Override
    public synchronized void doAppend(ILoggingEvent eventObject) {
	var msgCpy = eventObject.getMessage();
	subscribers.forEach(r -> Platform.runLater(() -> r.accept(msgCpy)));
    }

    // TODO: How to unsubscribe again?
    public static void subscribe(Consumer<String> logConsumer) {
	subscribers.add(logConsumer);
    }
}

