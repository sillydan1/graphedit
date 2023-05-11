package dk.gtz.graphedit.demo;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import dk.gtz.graphedit.logging.EditorLog;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Demo extends Application {
    private static Logger logger = (Logger)LoggerFactory.getLogger(Demo.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO: This is just a version of the baeldung tutorial. Replace with something that actually is correct!
        var loader = new FXMLLoader(Main.class.getResource("fxml/SearchController.fxml"));
        var page = (AnchorPane) loader.load();
        var scene = new Scene(page);

        primaryStage.setTitle("Title goes here");
        primaryStage.setScene(scene);
        primaryStage.show();
        ((Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).addAppender(new EditorLog());
    }

    @Override
    public void stop() {
        logger.trace("good bye");
    }

    public static void main(final String[] args) {
        launch(args);
    }
}

