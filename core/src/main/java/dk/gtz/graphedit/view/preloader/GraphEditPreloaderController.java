package dk.gtz.graphedit.view.preloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;

public class GraphEditPreloaderController {
    private static Logger logger = LoggerFactory.getLogger(GraphEditPreloaderController.class);
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label statusLabel;
    @FXML
    private Label projectNameLabel;
    @FXML
    private ImageView logo;

    public GraphEditPreloaderController() {

    }

    public void handleNotification(LoadStateNotification notification) {
        logger.trace(notification.getLoadStateMessage());
        statusLabel.setText(notification.getLoadStateMessage());
    }
}

