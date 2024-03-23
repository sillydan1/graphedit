package dk.gtz.graphedit.view;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
import dk.gtz.graphedit.model.lsp.ModelNotification;
import dk.gtz.graphedit.serialization.IMimeTypeChecker;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.serialization.JacksonModelSerializer;
import dk.gtz.graphedit.serialization.TikaMimeTypeChecker;
import dk.gtz.graphedit.spi.ILanguageServer;
import dk.gtz.graphedit.spi.IPluginsContainer;
import dk.gtz.graphedit.tool.ClipboardTool;
import dk.gtz.graphedit.tool.EdgeCreateTool;
import dk.gtz.graphedit.tool.EdgeDeleteTool;
import dk.gtz.graphedit.tool.IToolbox;
import dk.gtz.graphedit.tool.LintInspectorTool;
import dk.gtz.graphedit.tool.MassDeleteTool;
import dk.gtz.graphedit.tool.SelectTool;
import dk.gtz.graphedit.tool.Toolbox;
import dk.gtz.graphedit.tool.UnifiedModellingTool;
import dk.gtz.graphedit.tool.VertexCreateTool;
import dk.gtz.graphedit.tool.VertexDeleteTool;
import dk.gtz.graphedit.tool.VertexDragMoveTool;
import dk.gtz.graphedit.tool.ViewTool;
import dk.gtz.graphedit.util.EditorActions;
import dk.gtz.graphedit.util.IObservableUndoSystem;
import dk.gtz.graphedit.util.MouseTracker;
import dk.gtz.graphedit.util.ObservableTreeUndoSystem;
import dk.gtz.graphedit.util.TipLoader;
import dk.gtz.graphedit.viewmodel.FileBufferContainer;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ISelectable;
import dk.gtz.graphedit.viewmodel.LanguageServerCollection;
import dk.gtz.graphedit.viewmodel.LintContainer;
import dk.gtz.graphedit.viewmodel.SyntaxFactoryCollection;
import dk.gtz.graphedit.viewmodel.TipContainer;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelProject;
import dk.yalibs.yadi.DI;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
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
    private List<Thread> lspThreads;

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
        DI.get(IPluginsContainer.class).getEnabledPlugins().forEach(e -> Platform.runLater(e::onStart));
        DI.get(IPluginsContainer.class).getEnabledPlugins().forEach(e -> {
	    var t = new Thread(() -> {
		try {
		    DI.get(LanguageServerCollection.class).add(e.getLanguageServers());
		} catch(Exception ex) {
		    logger.error("could not load language servers for plugin '{}': {}", e.getName(), ex.getMessage(), ex);
		}
	    });
	    t.setName("lsp-init-" + e.getName());
	    t.start();
	});
	if(DI.get(ViewModelEditorSettings.class).showTips().get())
	    EditorActions.openTipOfTheDay();
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
	DI.add(IObservableUndoSystem.class, () -> new ObservableTreeUndoSystem());
	if(!DI.contains(IModelSerializer.class))
	    DI.add(IModelSerializer.class, new JacksonModelSerializer());
	DI.add(IBufferContainer.class, new FileBufferContainer(DI.get(IModelSerializer.class)));
	DI.add(LintContainer.class, new LintContainer());
	ObservableList<ISelectable> selectedElementsList = FXCollections.observableArrayList();
	DI.add("selectedElements", selectedElementsList);
	DI.add(TipContainer.class, TipLoader.loadTips());
    }

    private void setupLSPs(LanguageServerCollection servers, File projectFile, IBufferContainer buffers, LintContainer lints) {
	if(lspThreads == null)
	    lspThreads = new ArrayList<>();
	for(var lspThread : lspThreads) {
	    logger.trace("interrupting lsp thread {}", lspThread.getName());
	    lspThread.interrupt();
	}
	for(var server : servers.entrySet())
	    setupServer(server.getValue(), projectFile, buffers, lints);
	servers.addListener((MapChangeListener<String,ILanguageServer>)e -> {
	    if(e.wasAdded())
		setupServer(e.getValueAdded(), projectFile, buffers, lints);
	});
    }

    private void setupServer(ILanguageServer server, File projectFile, IBufferContainer buffers, LintContainer lints) {
	logger.trace("initializing language server {} {}", server.getServerName(), server.getServerVersion());
	server.initialize(projectFile, buffers);
	server.addNotificationCallback(this::logNotification);
	server.addDiagnosticsCallback(lints::replaceAll);
	logger.trace("starting thread for language server {} {}", server.getServerName(), server.getServerVersion());
	var t = new Thread(server::start);
	t.setName(server.getServerName());
	lspThreads.add(t);
	t.start();
	lints.replaceAll(server.getDiagnostics());
    }

    private void logNotification(ModelNotification n) {
	switch (n.level()) {
	    case DEBUG:
		logger.debug(n.message());
		break;
	    case ERROR:
		logger.error(n.message());
		break;
	    case INFO:
		logger.info(n.message());
		break;
	    case TRACE:
		logger.trace(n.message());
		break;
	    case WARNING:
		logger.warn(n.message());
		break;
	    default:
		logger.trace(n.level() + ": " + n.message());
		break;
	}
    }

    private void loadProject() throws Exception {
	try {
	    var settings = DI.get(ViewModelEditorSettings.class);
	    var projectFilePath = Path.of(settings.lastOpenedProject().get());
	    if(!projectFilePath.toFile().exists())
		throw new Exception("project file path does not exist '" + projectFilePath.toString() + "', loading tmp project instead");
	    var project = DI.get(IModelSerializer.class).deserializeProject(projectFilePath.toFile());
	    DI.add(ViewModelProject.class, new ViewModelProject(project, Optional.of(projectFilePath.toFile().getParent())));
	    setupLSPs(DI.get(LanguageServerCollection.class), projectFilePath.toFile(), DI.get(IBufferContainer.class), DI.get(LintContainer.class));
	} catch(Exception e) {
	    logger.error(e.getMessage(), e);
	    var newProject = new ViewModelProject(new ModelProject("MyGraphEditProject"), Optional.empty());
	    DI.add(ViewModelProject.class, newProject);
	    // Save the temp project and start the LSPs
	    var tmpPath = Path.of(newProject.rootDirectory().get() + File.separator + "tmp.json");
	    EditorActions.saveProject(newProject.toModel(), tmpPath); // NOTE: This does not set the project to the lastOpenedProject setting
	    setupLSPs(DI.get(LanguageServerCollection.class), tmpPath.toFile(), DI.get(IBufferContainer.class), DI.get(LintContainer.class));
	}
    }

    private void setupToolbox() {
	var toolbox = new Toolbox("edit", new UnifiedModellingTool(),
		new VertexDragMoveTool(),
		new EdgeCreateTool(),
		new EdgeDeleteTool(),
		new VertexCreateTool(),
		new VertexDeleteTool(),
		new SelectTool(),
		new LintInspectorTool(),
		new ClipboardTool(),
		new MassDeleteTool());
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
	if(lspThreads == null)
	    return;
	for(var lspThread : lspThreads) {
	    logger.trace("interrupting lsp thread {}", lspThread.getName());
	    lspThread.interrupt();
	}
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

