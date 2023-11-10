package dk.gtz.graphedit.view;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.view.log.Hyperlink;
import dk.gtz.graphedit.view.log.HyperlinkTextArea;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.IFocusable;
import dk.yalibs.yadi.DI;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;

/**
 * The javafx controller for a tab containing rich-text logs with support for markdown-style linking to syntax elements
 */
public class LogTabController extends StackPane {
    private static Logger logger = LoggerFactory.getLogger(LogTabController.class);
    private boolean autoscroll, wordwrap;
    private HyperlinkTextArea textArea;
    private VirtualizedScrollPane<HyperlinkTextArea> scrollPane;
    private Pattern linkMatcher;
    private IBufferContainer bufferContainer;

    /**
     * Create a new instance
     */
    public LogTabController() {
	linkMatcher = getPattern();
        bufferContainer = DI.get(IBufferContainer.class);
	initializeTextArea();
    }

    /**
     * Add a log message to the log view.
     * A log message can include markdown-style links to syntax elements' uuid.
     * 
     * Example:
     * <pre>
     * {@code
     * var logMessageWithALink = "error at [this vertex](f53ec9f0-dd57-4099-b3dd-3b72bda282df).";
     * }
     * </pre>
     * @param logMessage The log message to add
     */
    public void addLog(String logMessage) {
        if(logMessage == null) {
            logger.warn("null message occurred");
            return;
        }
        var matcher = linkMatcher.matcher(logMessage);
        var index = 0;
        while(matcher.find()) {
            textArea.appendText(logMessage.substring(index, matcher.start()));
            index = matcher.end();
            textArea.appendWithLink(matcher.group("display"), matcher.group("identifier"));
        }
        textArea.appendText(logMessage.substring(index) + "\n");
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
	textArea = new HyperlinkTextArea(this::onLinkClick, "log-text");
        scrollPane = new VirtualizedScrollPane<>(textArea);
        textArea.textProperty().addListener((e, o, n) -> {
            if (!autoscroll)
                return;
            scrollToLastLine();
        });
        textArea.getStyleClass().add("log-text");
        textArea.setEditable(false);
	getChildren().add(textArea);
    }

    private void onLinkClick(Hyperlink link) {
        try {
            var lookupId = UUID.fromString(link.getLink());
            var result = getFocusable(lookupId);
            result.ifPresent(IFocusable::focus);
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private Optional<IFocusable> getFocusable(UUID lookupId) {
        for(var buffer : bufferContainer.getBuffers().entrySet()) {
            var syntax = buffer.getValue().syntax();
            if(syntax.vertices().containsKey(lookupId))
                return Optional.of(syntax.vertices().get(lookupId));
            if(syntax.edges().containsKey(lookupId))
                return Optional.of(syntax.edges().get(lookupId));
        }
        return Optional.empty();
    }

    private void scrollToLastLine() {
        Platform.runLater(() -> scrollPane.scrollYBy(textArea.totalHeightEstimateProperty().getValue()));
    }
}

