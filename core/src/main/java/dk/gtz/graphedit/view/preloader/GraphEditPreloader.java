package dk.gtz.graphedit.view.preloader;

import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The default preloader for graphedit, containing some basic project management features
 */
public class GraphEditPreloader extends Preloader {
    private GraphEditPreloaderController controller;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
	var scene = loadMainScene();
	this.primaryStage = primaryStage;
	this.primaryStage.setScene(scene);
        this.primaryStage.initStyle(StageStyle.UNDECORATED);
	this.primaryStage.show();
    }

    private Scene loadMainScene() throws Exception {
	var loader = new FXMLLoader(getClass().getResource("GraphEditPreloader.fxml"));
	var page = (BorderPane) loader.load();
	controller = loader.getController();
	var screenBounds = Screen.getPrimary().getVisualBounds();
        var scene = new Scene(page, screenBounds.getWidth() * 0.8, screenBounds.getHeight() * 0.8);
	scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
	return scene;
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        if(info instanceof LoadStateNotification lsn)
            controller.handleNotification(lsn);
	if(info instanceof FinishNotification fn)
	    primaryStage.hide();
        super.handleApplicationNotification(info);
	if(!controller.isStarted())
	    controller.start();
    }
}

