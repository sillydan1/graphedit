package dk.gtz.graphedit.demo;

import java.util.HashMap;
import java.util.UUID;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.model.*;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.serialization.JacksonModelSerializer;

public class SerializationDemo {
    private static Logger logger = (Logger)LoggerFactory.getLogger(SerializationDemo.class);

    private static void serializationDemo() throws Exception {
        IModelSerializer serializer = new JacksonModelSerializer();
        var model = getExampleModel();
        var serModel = serializer.serialize(model);
        logger.info(serModel);
        var deserModel = serializer.deserialize(serModel);
        logger.info(deserModel.metadata().get("syntax"));
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

