package dk.gtz.graphedit.view;

import java.nio.file.Path;

import org.slf4j.LoggerFactory;

import atlantafx.base.controls.ModalPane;
import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import dk.gtz.graphedit.BuildConfig;
import dk.gtz.graphedit.exceptions.ProjectLoadException;
import dk.gtz.graphedit.logging.EditorLogAppender;
import dk.gtz.graphedit.logging.Toast;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.serialization.JacksonModelSerializer;
import dk.gtz.graphedit.tool.EdgeCreateTool;
import dk.gtz.graphedit.tool.EdgeDeleteTool;
import dk.gtz.graphedit.tool.EditorActions;
import dk.gtz.graphedit.tool.IToolbox;
import dk.gtz.graphedit.tool.SelectTool;
import dk.gtz.graphedit.tool.Toolbox;
import dk.gtz.graphedit.tool.VertexCreateTool;
import dk.gtz.graphedit.tool.VertexDeleteTool;
import dk.gtz.graphedit.tool.VertexDragMoveTool;
import dk.gtz.graphedit.tool.ViewTool;
import dk.gtz.graphedit.view.preloader.FinishNotification;
import dk.gtz.graphedit.view.preloader.GraphEditPreloader;
import dk.gtz.graphedit.view.preloader.LoadStateNotification;
import dk.gtz.graphedit.viewmodel.FileBufferContainer;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ISelectable;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelProject;
import dk.yalibs.yadi.DI;
import dk.yalibs.yaundo.IUndoSystem;
import dk.yalibs.yaundo.StackUndoSystem;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class GraphEditApplication extends Application implements IRestartableApplication {
    private static Logger logger = (Logger)LoggerFactory.getLogger(GraphEditApplication.class);
    private Stage primaryStage;

    public static void launchWithoutPreloader(final String[] args) {
	System.clearProperty("javafx.preloader");
	launch(args);
    }

    public static void launchUsingPreloader(final String[] args) {
	System.setProperty("javafx.preloader", GraphEditPreloader.class.getCanonicalName());
	launch(args);
    }

    @Override
    public void init() {
	DI.add(IRestartableApplication.class, this);
	setupApplication();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
	this.primaryStage = primaryStage;
	var settings = DI.get(ViewModelEditorSettings.class);
	notifyPreloader(new LoadStateNotification("starting"));
	if(settings.autoOpenLastProject().get())
	    kickoff(primaryStage);
    }

    private void kickoff(Stage primaryStage) throws Exception {
	try {
	    notifyPreloader(new LoadStateNotification("setup application"));
	    loadProject();
	    notifyPreloader(new LoadStateNotification("setup toolbox"));
	    setupToolbox();
	    notifyPreloader(new LoadStateNotification("setup preferences"));
	    setupPreferences();
	    notifyPreloader(new LoadStateNotification("setup logging"));
	    setupLogging();
	    notifyPreloader(new LoadStateNotification("setting the stage"));
	    setupStage(primaryStage);
	    notifyPreloader(new FinishNotification());
	} catch(ProjectLoadException e) {
	    logger.error("could not open project");
	    notifyPreloader(new LoadStateNotification(e.getMessage()));
	}
    }

    @Override
    public void restart() {
	primaryStage.close();
	try {
	    var newStage = new Stage();
	    kickoff(newStage);
	    primaryStage = newStage;
	} catch(Exception e) {
	    primaryStage.show();
	    logger.error(e.getMessage());
	}
    }

    private void setupApplication() {
	DI.add(MouseTracker.class, new MouseTracker(primaryStage, true));
	DI.add(IUndoSystem.class, new StackUndoSystem());
	DI.add(IModelSerializer.class, () -> new JacksonModelSerializer());
	DI.add(IBufferContainer.class, new FileBufferContainer(DI.get(IModelSerializer.class)));
	ObservableList<ISelectable> selectedElementsList = FXCollections.observableArrayList();
	DI.add("selectedElements", selectedElementsList);
	DI.add(ViewModelEditorSettings.class, EditorActions.loadEditorSettings());
    }

    private void loadProject() throws Exception {
	var settings = DI.get(ViewModelEditorSettings.class);
	var projectFilePath = Path.of(settings.lastOpenedProject().get());
	if(!projectFilePath.toFile().exists())
	    throw new ProjectLoadException("project file not found '%s'".formatted(projectFilePath.toString()));
	notifyPreloader(new LoadStateNotification("loading project file: '%s'".formatted(projectFilePath.toString())));
	var project = DI.get(IModelSerializer.class).deserializeProject(projectFilePath.toFile());
	DI.add(ViewModelProject.class, new ViewModelProject(project, projectFilePath.toFile().getParent()));
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
	var settings = DI.get(ViewModelEditorSettings.class);
	onUseLightThemeChange(settings.useLightTheme().get());
        settings.useLightTheme().addListener((e,o,n) -> onUseLightThemeChange(n));
    }

    private void onUseLightThemeChange(boolean useLightTheme) {
	if(useLightTheme)
	    Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
	else
	    Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
    }

    private void setupStage(Stage primaryStage) throws Exception {
	primaryStage.setTitle("%s %s".formatted(BuildConfig.APP_NAME, BuildConfig.APP_VERSION));
	setupModalPane();
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
	logger.trace("shutting down...");
    }

    private Scene loadMainScene() throws Exception {
	var loader = new FXMLLoader(EditorController.class.getResource("Editor.fxml"));
	var page = (StackPane) loader.load();
        var screenBounds = Screen.getPrimary().getVisualBounds();
        var scene = new Scene(page, screenBounds.getWidth() * 0.8, screenBounds.getHeight() * 0.8);
	scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
	Toast.initialize(page);
	page.getChildren().add(DI.get(ModalPane.class));
	return scene;
    }

    private void setupModalPane() {
	var modalPane = new ModalPane();
	modalPane.setId("modal pane");
	modalPane.displayProperty().addListener((e,o,n) -> {
	    if(n)
		return;
	    modalPane.setAlignment(Pos.CENTER);
	    modalPane.usePredefinedTransitionFactories(null);
	});
	DI.add(ModalPane.class, modalPane);
    }
}

