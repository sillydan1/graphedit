package dk.gtz.graphedit.view;

import java.util.List;

import dk.gtz.graphedit.viewmodel.LanguageServerCollection;
import dk.yalibs.yadi.DI;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class StatusBarController extends StackPane {
    private final HBox container;
    private Label spinnerLabel;
    private Label lspLabel;
    private Label messageLabel;
    private StringProperty spinnerString;
    private Thread spinnerThread;
    private int frameIndex;
    private final long intervalMilliseconds = 80;
    private final List<String> frames = List.of(
	    "\u280B",
	    "\u2819",
	    "\u2839",
	    "\u2838",
	    "\u283C",
	    "\u2834",
	    "\u2826",
	    "\u2827",
	    "\u2807",
	    "\u280F"); // Shamelessly stolen from cli-spinners, see https://github.com/sindresorhus/cli-spinners/blob/main/spinners.json

    public StatusBarController() {
	frameIndex = 0;
	spinnerThread = new Thread(this::setSpinnerLabel);
	spinnerThread.setDaemon(true);
	spinnerString = new SimpleStringProperty();
	container = new HBox();
	container.setPadding(new Insets(12));
	container.setSpacing(5);
	getChildren().add(container);
	initializeLabels();
	container.getChildren().addAll(spinnerLabel, lspLabel, messageLabel);
	initializeLSPs();
    }

    private void initializeLabels() {
	spinnerLabel = new Label();
	spinnerLabel.getStyleClass().add("status-bar-text");
	spinnerLabel.textProperty().bind(spinnerString);
	lspLabel = new Label();
	lspLabel.getStyleClass().add("status-bar-text");
	messageLabel = new Label();
	messageLabel.getStyleClass().add("status-bar-text");
    }

    private void initializeLSPs() {
	var lsps = DI.get(LanguageServerCollection.class);
	for(var server : lsps.values()) {
	    server.addProgressCallback(p -> {
		Platform.runLater(() -> {
		    lspLabel.setText(p.title()+":");
		    messageLabel.setText(p.message());
		});
		switch(p.type()) {
		    case PROGRESS:
		    case BEGIN:
			if(!spinnerThread.isAlive())
			    startSpinnerThread();
			break;
		    case END:
			if(spinnerThread.isAlive())
			    spinnerThread.interrupt();
			Platform.runLater(() -> spinnerString.set("\u2714")); // checkmark unicode character
			break;
		    default:
			if(spinnerThread.isAlive())
			    spinnerThread.interrupt();
			Platform.runLater(() -> spinnerString.set("?"));
			break;
		
		}
	    });
	}
    }

    private void startSpinnerThread() {
	spinnerThread = new Thread(this::setSpinnerLabel);
	spinnerThread.setDaemon(true);
	spinnerThread.start();
    }

    private void setSpinnerLabel() {
	while(!Thread.interrupted()) {
	    try {
		Platform.runLater(() -> {
		    spinnerString.set(frames.get(frameIndex));
		});
		frameIndex = (frameIndex + 1) % frames.size();
		Thread.sleep(intervalMilliseconds);
	    } catch (InterruptedException e) {
		return;
	    }
	}
    }
}
