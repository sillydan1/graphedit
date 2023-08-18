package dk.gtz.graphedit.view;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.view.log.Hyperlink;
import dk.gtz.graphedit.view.log.HyperlinkTextArea;
import dk.gtz.graphedit.view.log.TextStyle;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.IFocusable;
import dk.yalibs.yadi.DI;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;

public class LogTabController extends StackPane {
    private static Logger logger = LoggerFactory.getLogger(LogTabController.class);
    private boolean autoscroll, wordwrap;
    private HyperlinkTextArea textArea;
    private VirtualizedScrollPane<HyperlinkTextArea> scrollPane;
    private Pattern linkMatcher;
    private IBufferContainer bufferContainer;

    public LogTabController() {
	linkMatcher = getPattern();
        bufferContainer = DI.get(IBufferContainer.class);
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

    public void onLogAdded(String logMessage) {
        var matcher = linkMatcher.matcher(logMessage);
        var index = 0;
        while(matcher.find()) {
            textArea.appendText(logMessage.substring(index, matcher.start()));
            index = matcher.end();
            textArea.appendWithLink(matcher.group("display"), matcher.group("identifier"));
        }
        textArea.appendText(logMessage.substring(index) + "\n");
    }

    private void scrollToLastLine() {
        Platform.runLater(() -> scrollPane.scrollYBy(textArea.totalHeightEstimateProperty().getValue()));
    }
}

