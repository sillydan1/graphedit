package dk.gtz.graphedit.view;

import java.util.regex.Pattern;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.view.log.Hyperlink;
import dk.gtz.graphedit.view.log.HyperlinkTextArea;
import dk.gtz.graphedit.view.log.TextStyle;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;

public class LogTabController extends StackPane {
    private static Logger logger = LoggerFactory.getLogger(LogTabController.class);
    private boolean autoscroll, wordwrap;
    private HyperlinkTextArea textArea;
    private VirtualizedScrollPane<HyperlinkTextArea> scrollPane;
    private Pattern linkMatcher;

    public LogTabController() {
	linkMatcher = getPattern();
	initializeTextArea();
    }

    private Pattern getPattern() {
        // For humans:
	// identifiers are uuid v4's
        // [<display>](<identifier>)
	var uuidRegex = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
        var z = "\\[(?<display>[^]]+)]\\((?<identifier>"+uuidRegex+")\\)";
        return Pattern.compile(z);
    }

    private void initializeTextArea() {
	textArea = new HyperlinkTextArea(this::onLinkClick);
        scrollPane = new VirtualizedScrollPane<>(textArea);
        textArea.textProperty().addListener((e, o, n) -> {
            if (!autoscroll)
                return;
            scrollToLastLine();
        });
        textArea.setTextInsertionStyle(new TextStyle().updateTextColorWeb("#FFF")); // TODO: styling is odd/off - Look into how to use atlantafx's colorscheme
        textArea.getStyleClass().add("log-text");
        textArea.setEditable(false);
	getChildren().add(textArea);
    }

    private void onLinkClick(Hyperlink link) {
	logger.debug("you clicked on a link!");
    }

    public void onLogAdded(String logMessage) {
        var matcher = linkMatcher.matcher(logMessage);
        var index = 0;
        while(matcher.find()) {
            textArea.appendText(logMessage.substring(index, matcher.start()));
            index = matcher.end();
            textArea.appendWithLink(matcher.group("display"), matcher.group());
        }
        textArea.appendText(logMessage.substring(index) + "\n");
    }

    private void scrollToLastLine() {
        Platform.runLater(() -> scrollPane.scrollYBy(textArea.totalHeightEstimateProperty().getValue()));
    }
}

