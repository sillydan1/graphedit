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
import dk.gtz.graphedit.tool.ViewTool;
import dk.gtz.graphedit.undo.IUndoSystem;
import dk.gtz.graphedit.undo.StackUndoSystem;
import dk.gtz.graphedit.view.util.PreferenceUtil;
import dk.gtz.graphedit.viewmodel.FileBufferContainer;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelProject;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class GraphEditApplication extends Application {
    private static Logger logger = (Logger)LoggerFactory.getLogger(GraphEditApplication.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
	DI.add(MouseTracker.class, new MouseTracker(primaryStage, true));
	setupApplication();
	setupToolbox();
	setupPreferences();
	setupStage(primaryStage);
	setupLogging();
    }

    private void setupApplication() throws Exception {
	DI.add(IUndoSystem.class, new StackUndoSystem());
	DI.add(IModelSerializer.class, () -> new JacksonModelSerializer());
	DI.add(IBufferContainer.class, new FileBufferContainer(DI.get(IModelSerializer.class)));

	// TODO: add a project-picker preloader with a list of recent projects and a "just open my most recent thing"-toggle
	var projectDirectory = System.getProperty("user.dir") + File.separator + "project.json";
	logger.trace("loading project file: {}", projectDirectory);
	var mapper = ((JacksonModelSerializer)DI.get(IModelSerializer.class)).getMapper();
	var project = mapper.readValue(new File(projectDirectory), ModelProject.class);
	DI.add(ViewModelProject.class, new ViewModelProject(project));
    }

    private void setupToolbox() {
	DI.add(IToolbox.class, () -> {
	    var toolbox = new Toolbox("", new ViewTool());
	    toolbox.addDefaultTool(new SelectTool());
	    toolbox.add(new EdgeCreateTool());
	    toolbox.add(new EdgeDeleteTool());
	    toolbox.add(new VertexCreateTool());
	    return toolbox;
	}); 
    }

    private void setupPreferences() {
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
    }

    @Override
    public void stop() {
	// TODO: Something along the lines of "save and exit? yes/no"
	logger.trace("shutting down...");
    }

    public static void main(final String[] args) {
	launch(args);
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

