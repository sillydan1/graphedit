package dk.gtz.graphedit.demo;

import java.util.HashMap;
import java.util.UUID;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.model.ModelGraph;
import dk.gtz.graphedit.model.ModelPoint;
import dk.gtz.graphedit.model.ModelProjectResource;
import dk.gtz.graphedit.model.ModelVertex;
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

    private static ModelProjectResource getExampleModel() {
        var decls = "a := 0";
        var vertices = new HashMap<UUID,ModelVertex>();
        vertices.put(UUID.randomUUID(), new ModelVertex(new ModelPoint(0,0)));

        var edges = new HashMap<UUID,ModelEdge>();
        edges.put(UUID.randomUUID(), new ModelEdge(UUID.randomUUID(), UUID.randomUUID()));

        var model = new ModelGraph(decls, vertices, edges);

        var metaData = new HashMap<String,String>();
        metaData.put("syntax", "exampleSyntax");
        metaData.put("version", "v1.0.0");
        return new ModelProjectResource(metaData, model);
    }
}

