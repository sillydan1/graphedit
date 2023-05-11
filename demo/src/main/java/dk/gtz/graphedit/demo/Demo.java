package dk.gtz.graphedit.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Demo extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO: This is just a version of the baeldung tutorial. Replace with something that actually is correct!
        var loader = new FXMLLoader(Main.class.getResource("fxml/SearchController.fxml"));
        var page = (AnchorPane) loader.load();
        var scene = new Scene(page);

        primaryStage.setTitle("Title goes here");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }
}

