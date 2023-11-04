package dk.gtz.graphedit.view;

import java.nio.file.Path;
import java.util.Optional;

import org.slf4j.LoggerFactory;

import atlantafx.base.controls.ModalPane;
import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import dk.gtz.graphedit.logging.EditorLogAppender;
import dk.gtz.graphedit.logging.Toast;
import dk.gtz.graphedit.model.ModelProject;
import dk.gtz.graphedit.serialization.IMimeTypeChecker;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.serialization.JacksonModelSerializer;
import dk.gtz.graphedit.serialization.TikaMimeTypeChecker;
import dk.gtz.graphedit.tool.EdgeCreateTool;
import dk.gtz.graphedit.tool.EdgeDeleteTool;
import dk.gtz.graphedit.tool.EditorActions;
import dk.gtz.graphedit.tool.IToolbox;
import dk.gtz.graphedit.tool.SelectTool;
import dk.gtz.graphedit.tool.Toolbox;
import dk.gtz.graphedit.tool.UnifiedModellingTool;
import dk.gtz.graphedit.tool.VertexCreateTool;
import dk.gtz.graphedit.tool.VertexDeleteTool;
import dk.gtz.graphedit.tool.VertexDragMoveTool;
import dk.gtz.graphedit.tool.ViewTool;
import dk.gtz.graphedit.view.util.IObservableUndoSystem;
import dk.gtz.graphedit.view.util.ObservableStackUndoSystem;
import dk.gtz.graphedit.viewmodel.FileBufferContainer;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ISelectable;
import dk.gtz.graphedit.viewmodel.SyntaxFactoryCollection;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelProject;
import dk.yalibs.yadi.DI;
import dk.yalibs.yaundo.IUndoSystem;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * The primary entrypoint class for Graphedit.
 */
public class GraphEditApplication extends Application implements IRestartableApplication {
    private static Logger logger = (Logger)LoggerFactory.getLogger(GraphEditApplication.class);
    private Stage primaryStage;

    public static void launchApp(final String[] args) {
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
	if(settings.autoOpenLastProject().get())
	    kickoff(primaryStage);
    }

    private void kickoff(Stage primaryStage) throws Exception {
	loadProject();
	setupToolbox();
	setupPreferences();
	setupLogging();
	setupStage(primaryStage);
	DI.add(Window.class, primaryStage.getScene().getWindow());
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
	DI.add(IMimeTypeChecker.class, new TikaMimeTypeChecker());
	var undoSystem = new ObservableStackUndoSystem();
	DI.add(IUndoSystem.class, undoSystem);
	DI.add(IObservableUndoSystem.class, undoSystem); // TODO: Use this - This relates to https://github.com/sillydan1/graphedit/issues/1
	DI.add(IModelSerializer.class, () -> new JacksonModelSerializer());
	DI.add(IBufferContainer.class, new FileBufferContainer(DI.get(IModelSerializer.class)));
	ObservableList<ISelectable> selectedElementsList = FXCollections.observableArrayList();
	DI.add("selectedElements", selectedElementsList);
	DI.add(ViewModelEditorSettings.class, EditorActions.loadEditorSettings());
    }

    private void loadProject() throws Exception {
	try {
	    var settings = DI.get(ViewModelEditorSettings.class);
	    var projectFilePath = Path.of(settings.lastOpenedProject().get());
	    if(!projectFilePath.toFile().exists())
		throw new Exception("not a valid project file path, will load temp project " + projectFilePath.toString());
	    var project = DI.get(IModelSerializer.class).deserializeProject(projectFilePath.toFile());
	    DI.add(ViewModelProject.class, new ViewModelProject(project, Optional.of(projectFilePath.toFile().getParent())));
	} catch(Exception e) {
	    logger.error(e.getMessage(), e);
	    DI.add(ViewModelProject.class, new ViewModelProject(new ModelProject("MyGraphEditProject"), Optional.empty()));
	}
    }

    private void setupToolbox() {
	var toolbox = new Toolbox("edit", new UnifiedModellingTool(),
		new VertexDragMoveTool(),
		new EdgeCreateTool(),
		new EdgeDeleteTool(),
		new VertexCreateTool(),
		new VertexDeleteTool(),
		new SelectTool());
	toolbox.add("inspect", new ViewTool());
	DI.add(IToolbox.class, toolbox);
	toolbox.selectTool(toolbox.getDefaultTool());
    }

    private void setupPreferences() {
	if(!DI.contains(SyntaxFactoryCollection.class))
	    DI.add(SyntaxFactoryCollection.class, new SyntaxFactoryCollection());

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
	var project = DI.get(ViewModelProject.class);
	primaryStage.setTitle("%s %s".formatted("Graphedit", project.name().get()));
	project.name().addListener((e,o,n) -> primaryStage.setTitle("%s %s".formatted("Graphedit", n)));
	setupModalPane();
	primaryStage.setScene(loadMainScene());
	primaryStage.show();
    }

    private void setupLogging() {
	((Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).addAppender(new EditorLogAppender());
	EditorLogAppender.subscribe(Level.INFO, Toast::info);
	EditorLogAppender.subscribe(Level.WARN, Toast::warn);
	EditorLogAppender.subscribe(Level.ERROR, Toast::error);
	EditorLogAppender.subscribe(Level.TRACE, Toast::trace);
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

