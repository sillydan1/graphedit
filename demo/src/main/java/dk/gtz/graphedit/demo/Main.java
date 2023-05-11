package dk.gtz.graphedit.demo;

import java.util.HashMap;
import java.util.UUID;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import dk.gtz.graphedit.logging.EditorLog;
import dk.gtz.graphedit.model.*;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.serialization.JacksonModelSerializer;

public class Main {
    private static Logger logger = (Logger)LoggerFactory.getLogger(Main.class);

    public static void main(String[] argv) throws Exception {
        var args = new Args();
        var b = JCommander.newBuilder()
            .programName(BuildConfig.APP_NAME)
            .acceptUnknownOptions(true)
            .addObject(args)
            .build();
        b.parse(argv);
        ((Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.toLevel(args.verbosity));
        if(args.help) {
            b.usage();
            return;
        }
        EditorLog.subscribe(msg -> { System.out.println("A message happenned!: " + msg); });
        logger.info("welcome to {} {}", BuildConfig.APP_NAME, BuildConfig.APP_VERSION);
        Demo.main(argv);
    }

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

