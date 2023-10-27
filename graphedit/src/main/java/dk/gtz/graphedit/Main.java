package dk.gtz.graphedit;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import dk.gtz.graphedit.syntaxes.lts.LTSSyntaxFactory;
import dk.gtz.graphedit.syntaxes.petrinet.PNSyntaxFactory;
import dk.gtz.graphedit.syntaxes.text.TextSyntaxFactory;
import dk.gtz.graphedit.view.GraphEditApplication;
import dk.gtz.graphedit.view.ISyntaxFactory;
import dk.yalibs.yadi.DI;

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
        // TODO: This should be an interface
        var factories = new HashMap<String, ISyntaxFactory>();
        addSyntaxFactory(factories, new TextSyntaxFactory());
        addSyntaxFactory(factories, new LTSSyntaxFactory());
        addSyntaxFactory(factories, new PNSyntaxFactory());
        DI.add("syntax_factories", factories);
        logger.info("welcome to {} {}", BuildConfig.APP_NAME, BuildConfig.APP_VERSION);
        GraphEditApplication.launchApp(argv);
        logger.info("goodbye from {} {}", BuildConfig.APP_NAME, BuildConfig.APP_VERSION);
    }

    private static void addSyntaxFactory(Map<String,ISyntaxFactory> map, ISyntaxFactory factory) {
        map.put(factory.getSyntaxName(), factory);
    }
}

