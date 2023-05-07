package dk.gtz.graphedit.demo;

import java.util.HashMap;
import java.util.UUID;

import com.beust.jcommander.JCommander;
import dk.gtz.graphedit.model.*;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.serialization.JacksonModelSerializer;

public class Main {
    // TODO: This should be written as a unit test! (most likely, everything we demo here should be unit tested)
    public static void main(String[] argv) throws Exception {
        var args = new Args();
        var b = JCommander.newBuilder()
            .addObject(args)
            .programName("demo")
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

