package dk.gtz.graphedit;

import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import dk.gtz.graphedit.plugins.PluginLoader;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.serialization.JacksonModelSerializer;
import dk.gtz.graphedit.util.EditorActions;
import dk.gtz.graphedit.view.GraphEditApplication;
import dk.gtz.graphedit.viewmodel.LanguageServerCollection;
import dk.gtz.graphedit.viewmodel.SyntaxFactoryCollection;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.yalibs.yadi.DI;

public class Main {
	private static Logger logger = (Logger) LoggerFactory.getLogger(Main.class);

	public static void main(String[] argv) throws Exception {
		var args = new Args();
		var builder = JCommander.newBuilder()
				.programName(BuildConfig.APP_NAME)
				.acceptUnknownOptions(true)
				.addObject(args)
				.build();
		builder.parse(argv);
		((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.toLevel(args.verbosity));
		((Logger) LoggerFactory.getLogger("io.grpc")).setLevel(Level.toLevel(args.grpcVerbosity));
		if (args.help) {
			builder.usage();
			return;
		}

		var factories = new SyntaxFactoryCollection();
		var servers = new LanguageServerCollection();
		DI.add(SyntaxFactoryCollection.class, factories);
		DI.add(LanguageServerCollection.class, servers);
		DI.add(IModelSerializer.class, new JacksonModelSerializer());
		var editorSettings = EditorActions.loadEditorSettings();
		DI.add(ViewModelEditorSettings.class, editorSettings);
		var loader = new PluginLoader(args.pluginDirs, DI.get(IModelSerializer.class));
		DI.add(PluginLoader.class, loader);

		logger.info("welcome to {} {}", BuildConfig.APP_NAME, BuildConfig.APP_VERSION);
		GraphEditApplication.launchApp(argv);
		logger.info("goodbye from {} {}", BuildConfig.APP_NAME, BuildConfig.APP_VERSION);
	}
}
