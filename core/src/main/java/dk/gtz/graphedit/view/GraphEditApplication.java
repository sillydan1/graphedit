package dk.gtz.graphedit.view;

import org.slf4j.LoggerFactory;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import ch.qos.logback.classic.Logger;
import dk.gtz.graphedit.logging.EditorLog;
import dk.gtz.graphedit.BuildConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GraphEditApplication extends Application {
    private static Logger logger = (Logger)LoggerFactory.getLogger(GraphEditApplication.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
	Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
	Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

	var loader = new FXMLLoader(EditorController.class.getResource("Editor.fxml"));
	var page = (StackPane) loader.load();
	var scene = new Scene(page);

	primaryStage.setTitle("%s %s".formatted(BuildConfig.APP_NAME, BuildConfig.APP_VERSION));
	primaryStage.setScene(scene);
	primaryStage.show();
	((Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).addAppender(new EditorLog());
    }

    @Override
    public void stop() {
	logger.trace("shutting down...");
    }

    public static void main(final String[] args) {
	launch(args);
    }

}

