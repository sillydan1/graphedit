package dk.gtz.graphedit.view;

import java.io.File;

import org.slf4j.LoggerFactory;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import dk.gtz.graphedit.BuildConfig;
import dk.gtz.graphedit.logging.EditorLogAppender;
import dk.gtz.graphedit.logging.Toast;
import dk.gtz.graphedit.model.ModelProject;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.serialization.JacksonModelSerializer;
import dk.gtz.graphedit.skyhook.DI;
import dk.gtz.graphedit.tool.EdgeCreateTool;
import dk.gtz.graphedit.tool.EdgeDeleteTool;
import dk.gtz.graphedit.tool.IToolbox;
import dk.gtz.graphedit.tool.SelectTool;
import dk.gtz.graphedit.tool.Toolbox;
import dk.gtz.graphedit.tool.VertexCreateTool;
import dk.gtz.graphedit.tool.VertexDeleteTool;
import dk.gtz.graphedit.tool.VertexDragMoveTool;
import dk.gtz.graphedit.tool.ViewTool;
import dk.gtz.graphedit.undo.IUndoSystem;
import dk.gtz.graphedit.undo.StackUndoSystem;
import dk.gtz.graphedit.view.preloader.GraphEditPreloader;
import dk.gtz.graphedit.view.preloader.LoadStateNotification;
import dk.gtz.graphedit.view.preloader.FinishNotification;
import dk.gtz.graphedit.view.util.PreferenceUtil;
import dk.gtz.graphedit.viewmodel.FileBufferContainer;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ISelectable;
import dk.gtz.graphedit.viewmodel.ViewModelProject;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class GraphEditApplication extends Application {
    private static Logger logger = (Logger)LoggerFactory.getLogger(GraphEditApplication.class);

    public static void launchWitoutPreloader(final String[] args) {
	launch(args);
    }

    public static void launchUsingPreloader(final String[] args) {
	System.setProperty("javafx.preloader", GraphEditPreloader.class.getCanonicalName());
	launch(args);
    }

    @Override
    public void init() {
	// This is run before the preloader is loaded
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
	notifyPreloader(new LoadStateNotification("starting"));
	DI.add(MouseTracker.class, new MouseTracker(primaryStage, true));
	notifyPreloader(new LoadStateNotification("setup application"));
	setupApplication();
	notifyPreloader(new LoadStateNotification("setup toolbox"));
	setupToolbox();
	notifyPreloader(new LoadStateNotification("setup preferences"));
	setupPreferences();
	notifyPreloader(new LoadStateNotification("setup logging"));
	setupLogging();
	notifyPreloader(new LoadStateNotification("setting the stage"));
	setupStage(primaryStage);
	notifyPreloader(new FinishNotification());
    }

    private void setupApplication() throws Exception {
	DI.add(IUndoSystem.class, new StackUndoSystem());
	DI.add(IModelSerializer.class, () -> new JacksonModelSerializer());
	DI.add(IBufferContainer.class, new FileBufferContainer(DI.get(IModelSerializer.class)));
	ObservableList<ISelectable> selectedElementsList = FXCollections.observableArrayList();
	DI.add("selectedElements", selectedElementsList);

	// TODO: add a project-picker to the preloader with a list of recent projects and a "just open my most recent thing"-toggle
	var projectDirectory = System.getProperty("user.dir") + File.separator + "project.json";
	notifyPreloader(new LoadStateNotification("loading project file: '%s'".formatted(projectDirectory)));
	var mapper = ((JacksonModelSerializer)DI.get(IModelSerializer.class)).getMapper();
	var project = mapper.readValue(new File(projectDirectory), ModelProject.class);
	DI.add(ViewModelProject.class, new ViewModelProject(project));
    }

    private void setupToolbox() {
	DI.add(IToolbox.class, () -> {
	    var toolbox = new Toolbox("inspect", new ViewTool());
	    toolbox.addDefaultTool(new SelectTool());
	    toolbox.add("edit",
		    new VertexDragMoveTool(),
		    new EdgeCreateTool(),
		    new EdgeDeleteTool(),
		    new VertexCreateTool(),
		    new VertexDeleteTool());
	    return toolbox;
	}); 
    }

    private void setupPreferences() {
	DI.add(ISyntaxFactory.class, new DemoSyntaxFactory());
	var useLightTheme = PreferenceUtil.getUseLightTheme();
	if(useLightTheme)
	    Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
	else
	    Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
    }

    private void setupStage(Stage primaryStage) throws Exception {
	primaryStage.setTitle("%s %s".formatted(BuildConfig.APP_NAME, BuildConfig.APP_VERSION));
	primaryStage.setScene(loadMainScene());
	primaryStage.show();
    }

    private void setupLogging() {
	((Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).addAppender(new EditorLogAppender());
	EditorLogAppender.subscribe(Level.ERROR, Toast::error);
	EditorLogAppender.subscribe(Level.WARN, Toast::warn);
	EditorLogAppender.subscribe(Level.INFO, Toast::info);
	Thread.setDefaultUncaughtExceptionHandler((t,e) -> logger.error("Uncaught error: %s".formatted(e.getMessage()), e));
    }

    @Override
    public void stop() {
	// TODO: Something along the lines of "save and exit? yes/no"
	logger.trace("shutting down...");
    }

    private Scene loadMainScene() throws Exception {
	var loader = new FXMLLoader(EditorController.class.getResource("Editor.fxml"));
	var page = (StackPane) loader.load();
        var screenBounds = Screen.getPrimary().getVisualBounds();
        var scene = new Scene(page, screenBounds.getWidth() * 0.8, screenBounds.getHeight() * 0.8);
	scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
	Toast.initialize(page);
	return scene;
    }
}

