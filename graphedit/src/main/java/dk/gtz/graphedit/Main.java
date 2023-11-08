package dk.gtz.graphedit;

import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import dk.gtz.graphedit.plugins.PluginLoader;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.serialization.JacksonModelSerializer;
import dk.gtz.graphedit.spi.IPluginsContainer;
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

        DI.add(IModelSerializer.class, new JacksonModelSerializer());
        var loader = new PluginLoader(args.pluginDirs, DI.get(IModelSerializer.class)).loadPlugins();
        var factories = new SyntaxFactoryCollection();
        DI.add(SyntaxFactoryCollection.class, factories);
        DI.add(IPluginsContainer.class, loader.getLoadedPlugins());
        for(var plugin : loader.getLoadedPlugins().getPlugins()) {
            try {
                plugin.onInitialize();
                factories.add(plugin.getSyntaxFactories());
            } catch (Exception e) {
                logger.error("could not load syntax factories for plugin: {}", plugin.getName(), e);
            }
        }
        if(factories.isEmpty())
            throw new Exception("Refusing to start the editor without any syntaxes. Please check your plugins directory");

        logger.info("welcome to {} {}", BuildConfig.APP_NAME, BuildConfig.APP_VERSION);
        GraphEditApplication.launchApp(argv);
        logger.info("goodbye from {} {}", BuildConfig.APP_NAME, BuildConfig.APP_VERSION);
    }
}

