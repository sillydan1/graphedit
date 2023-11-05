package dk.gtz.graphedit;

import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import dk.gtz.graphedit.plugins.PluginLoader;
import dk.gtz.graphedit.spi.IPluginsContainer;
import dk.gtz.graphedit.syntaxes.lts.LTSSyntaxFactory;
import dk.gtz.graphedit.syntaxes.petrinet.PNSyntaxFactory;
import dk.gtz.graphedit.syntaxes.text.TextSyntaxFactory;
import dk.gtz.graphedit.view.GraphEditApplication;
import dk.gtz.graphedit.viewmodel.SyntaxFactoryCollection;
import dk.yalibs.yadi.DI;

public class Main {
    private static Logger logger = (Logger)LoggerFactory.getLogger(Main.class);

    public static void main(String[] argv) throws Exception {
        var args = new Args();
        var builder = JCommander.newBuilder()
            .programName(BuildConfig.APP_NAME)
            .acceptUnknownOptions(true)
            .addObject(args)
            .build();
        builder.parse(argv);
        ((Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.toLevel(args.verbosity));
        if(args.help) {
            builder.usage();
            return;
        }

        var loader = new PluginLoader(args.pluginDirs).loadPlugins();
        var factories = new SyntaxFactoryCollection();
        DI.add(SyntaxFactoryCollection.class, factories);
        DI.add(IPluginsContainer.class, loader.getLoadedPlugins());
        for(var plugin : loader.getLoadedPlugins().getPlugins()) {
            try {
                factories.add(plugin.getSyntaxFactories());
            } catch (Exception e) {
                logger.error("could not load syntax factories for plugin: {}", plugin.getName(), e);
            }
        }

        factories.add(new TextSyntaxFactory()); // TODO: Extract this into a plugin
        factories.add(new LTSSyntaxFactory()); // TODO: Extract this into a plugin
        factories.add(new PNSyntaxFactory()); // TODO: Extract this into a plugin

        logger.info("welcome to {} {}", BuildConfig.APP_NAME, BuildConfig.APP_VERSION);
        GraphEditApplication.launchApp(argv);
        logger.info("goodbye from {} {}", BuildConfig.APP_NAME, BuildConfig.APP_VERSION);
    }
}

