package dk.gtz.graphedit.view;

import org.slf4j.LoggerFactory;

import atlantafx.base.theme.NordDark;
import ch.qos.logback.classic.Logger;
import dk.gtz.graphedit.BuildConfig;
import dk.gtz.graphedit.logging.EditorLogAppender;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.serialization.JacksonModelSerializer;
import dk.gtz.graphedit.skyhook.DI;
import dk.gtz.graphedit.viewmodel.FileBufferContainer;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GraphEditApplication extends Application {
    private static Logger logger = (Logger)LoggerFactory.getLogger(GraphEditApplication.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
	setupApplication();
	Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());

	var loader = new FXMLLoader(EditorController.class.getResource("Editor.fxml"));
	var page = (StackPane) loader.load();
	var scene = new Scene(page);

	primaryStage.setTitle("%s %s".formatted(BuildConfig.APP_NAME, BuildConfig.APP_VERSION));
	primaryStage.setScene(scene);
	primaryStage.show();
	((Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).addAppender(new EditorLogAppender());
    }

    private void setupApplication() {
	DI.add(IModelSerializer.class, () -> new JacksonModelSerializer());
	DI.add(IBufferContainer.class, new FileBufferContainer(DI.get(IModelSerializer.class)));
    }

    @Override
    public void stop() {
	// TODO: Something along the lines of "save and exit? yes/no"
	logger.trace("shutting down...");
    }

    public static void main(final String[] args) {
	launch(args);
    }
}

