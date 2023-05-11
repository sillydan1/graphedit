package dk.gtz.graphedit.demo;

import java.util.HashMap;
import java.util.UUID;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import com.beust.jcommander.JCommander;
import dk.gtz.graphedit.model.*;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.serialization.JacksonModelSerializer;

public class Main extends Application {
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

    public static void main(String[] argv) throws Exception {
        var args = new Args();
        var b = JCommander.newBuilder()
            .programName("demo")
            .acceptUnknownOptions(true)
            .addObject(args)
            .build();
        b.parse(argv);
        if(args.help) {
            b.usage();
            return;
        }

        IModelSerializer serializer = new JacksonModelSerializer();
        var model = getExampleModel();
        var serModel = serializer.serialize(model);
        System.out.println(serModel);
        var deserModel = serializer.deserialize(serModel);
        System.out.println(deserModel.metadata().get("syntax"));

        launch(argv);
    }

    private static Model getExampleModel() {
        var decls = "a := 0";
        var vertices = new HashMap<UUID,Vertex>();
        vertices.put(UUID.randomUUID(), new Vertex());

        var edges = new HashMap<UUID,Edge>();
        edges.put(UUID.randomUUID(), new Edge());

        var model = new Graph(decls, vertices, edges);

        var metaData = new HashMap<String,String>();
        metaData.put("syntax", "exampleSyntax");
        metaData.put("version", "v1.0.0");
        return new Model(metaData, model);
    }
}

