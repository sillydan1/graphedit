package dk.gtz.graphedit.plugins.view;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchService;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.StackedFontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import dk.gtz.graphedit.logging.Toast;
import dk.gtz.graphedit.serialization.IMimeTypeChecker;
import dk.gtz.graphedit.spi.IPluginsContainer;
import dk.gtz.graphedit.util.EditorActions;
import dk.gtz.graphedit.util.PlatformUtils;
import dk.gtz.graphedit.viewmodel.ViewModelProject;
import dk.yalibs.yadi.DI;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.PopupWindow.AnchorLocation;

public class ProjectFilesViewController extends VBox {
	private static record FileTreeEntry(Path path) {
		@Override
		public String toString() {
			return path.getFileName().toString();
		}
	}

	private static Logger logger = LoggerFactory.getLogger(ProjectFilesViewController.class);
	private ViewModelProject openProject;
	private TreeView<FileTreeEntry> fileTree;
	private WatchService watchService;
	private SimpleBooleanProperty useGitignoreMatcher;
	private GlobFileMatcher grapheditIgnoreMatcher;
	private SimpleBooleanProperty useGrapheditIgnoreMatcher;
	private SimpleBooleanProperty showHiddenFiles;
	private boolean isGitInstalled;
	private Thread watcherThread;

	public ProjectFilesViewController() {
		initialize();
	}

	private void initialize() {
		isGitInstalled = PlatformUtils.isProgramInstalled("git");
		openProject = DI.get(ViewModelProject.class);
		useGitignoreMatcher = new SimpleBooleanProperty(true);
		useGrapheditIgnoreMatcher = new SimpleBooleanProperty(true);
		showHiddenFiles = new SimpleBooleanProperty(false);
		initializeGlobMatchers();
		fileTree = createTreeView(openProject.name().get(), Path.of(openProject.rootDirectory().get()));
		var toolbar = createToolbar();
		getChildren().clear();
		getChildren().addAll(toolbar, fileTree);
		watchForFileTreeChanges();
		openProject.rootDirectory().addListener((e, o, n) -> {
			initialize();
		});
	}

	private void initializeGlobMatchers() {
		try {
			var root = Path.of(openProject.rootDirectory().get());
			var ignoreFile = Path.of(root.toString() + "/.graphedit.ignore");
			grapheditIgnoreMatcher = new GlobFileMatcher(root, ignoreFile);
		} catch (IOException ignored) {
			logger.debug(ignored.getMessage());
			grapheditIgnoreMatcher = new GlobFileMatcher();
		}
	}

	private boolean isGitignored(Path path) {
		try {
			if (!isGitInstalled)
				return false;
			var p = new ProcessBuilder("git", "check-ignore", "-q", path.toString());
			p.directory(Path.of(openProject.rootDirectory().get()).toFile());
			var pp = p.start();
			return pp.waitFor() == 0;
		} catch (InterruptedException | IOException e) {
			logger.error("git check-ignore command failed", e);
		}
		return false;
	}

	private Node createToolbar() {
		var gitignoreHideButton = new ToggleButton(null, new FontIcon(BootstrapIcons.GITHUB));
		gitignoreHideButton.selectedProperty().set(!useGitignoreMatcher.get());
		useGitignoreMatcher.bind(gitignoreHideButton.selectedProperty().not());
		var gitignoreTip = new Tooltip("Show gitignored files");
		gitignoreTip.setAnchorLocation(AnchorLocation.WINDOW_TOP_LEFT);
		gitignoreTip.setPrefWidth(200);
		gitignoreTip.setWrapText(true);
		gitignoreHideButton.setTooltip(gitignoreTip);

		var grapheditIgnoreHideButton = new ToggleButton(null, new FontIcon(BootstrapIcons.SHARE));
		grapheditIgnoreHideButton.selectedProperty().set(!useGrapheditIgnoreMatcher.get());
		useGrapheditIgnoreMatcher.bind(grapheditIgnoreHideButton.selectedProperty().not());
		var grapheditIgnoreTip = new Tooltip("Show graphedit ignored files");
		grapheditIgnoreTip.setAnchorLocation(AnchorLocation.WINDOW_TOP_LEFT);
		grapheditIgnoreTip.setPrefWidth(200);
		grapheditIgnoreTip.setWrapText(true);
		grapheditIgnoreHideButton.setTooltip(grapheditIgnoreTip);

		var showHiddenFilesButton = new ToggleButton(null, new FontIcon(BootstrapIcons.EYE));
		showHiddenFilesButton.selectedProperty().set(showHiddenFiles.get());
		showHiddenFiles.bind(showHiddenFilesButton.selectedProperty());
		var showHiddenFilesTip = new Tooltip("Show hidden files");
		showHiddenFilesTip.setAnchorLocation(AnchorLocation.WINDOW_TOP_LEFT);
		showHiddenFilesTip.setPrefWidth(200);
		showHiddenFilesTip.setWrapText(true);
		showHiddenFilesButton.setTooltip(showHiddenFilesTip);

		var newFileButton = new Button(null, new FontIcon(BootstrapIcons.FILE_EARMARK_PLUS));
		var newFileTip = new Tooltip("New model file");
		newFileTip.setAnchorLocation(AnchorLocation.WINDOW_TOP_LEFT);
		newFileTip.setPrefWidth(200);
		newFileTip.setWrapText(true);
		newFileButton.setTooltip(newFileTip);
		newFileButton.setOnAction(e -> EditorActions.createNewModelFile());

		var manualRefreshButton = new Button(null, new FontIcon(BootstrapIcons.ARROW_CLOCKWISE));
		var refreshTip = new Tooltip("Refresh");
		refreshTip.setAnchorLocation(AnchorLocation.WINDOW_TOP_LEFT);
		refreshTip.setPrefWidth(200);
		refreshTip.setWrapText(true);
		manualRefreshButton.setTooltip(refreshTip);
		manualRefreshButton.setOnAction(e -> updateTreeView());

		var exportButton = new MenuButton(null, new FontIcon(BootstrapIcons.BOX_ARROW_UP));
		exportButton.getStyleClass().addAll(Tweaks.NO_ARROW);
		for (var plugin : DI.get(IPluginsContainer.class).getEnabledPlugins()) {
			for (var exporter : plugin.getExporters()) {
				var menuItem = new MenuItem(exporter.getName());
				menuItem.setOnAction(e -> {
					var files = fileTree.getSelectionModel().getSelectedItems().stream()
							.map(v -> v.getValue().path().toFile()).toList();
					if (files.isEmpty()) {
						logger.info("No files selected");
						return;
					}
					EditorActions.exportFiles(exporter, files);
				});
				exportButton.getItems().add(menuItem);
			}
		}
		var exportTip = new Tooltip("Export selected files");
		exportTip.setAnchorLocation(AnchorLocation.WINDOW_TOP_LEFT);
		exportTip.setPrefWidth(200);
		exportTip.setWrapText(true);
		exportButton.setTooltip(exportTip);

		var result = new ToolBar(
				newFileButton,
				new Separator(Orientation.VERTICAL),
				grapheditIgnoreHideButton, showHiddenFilesButton, gitignoreHideButton,
				new Separator(Orientation.VERTICAL),
				manualRefreshButton, exportButton);
		result.setOrientation(Orientation.HORIZONTAL);
		return result;
	}

	private Node getPathFontIcon(Path path) {
		try {
			var mimeTypeChecker = DI.get(IMimeTypeChecker.class);
			if (isGitignored(path))
				return createStackedFontIcon(new FontIcon(BootstrapIcons.SLASH),
						IconUtils.getFileTypeIcon(mimeTypeChecker.getMimeType(path)));
			if (grapheditIgnoreMatcher.matches(path))
				return createStackedFontIcon(new FontIcon(BootstrapIcons.SLASH),
						IconUtils.getFileTypeIcon(mimeTypeChecker.getMimeType(path)));
			if (Files.isDirectory(path))
				return new FontIcon(BootstrapIcons.FOLDER);
			return IconUtils.getFileTypeIcon(mimeTypeChecker.getMimeType(path));
		} catch (IOException e) {
			logger.error(e.getMessage());
			return new FontIcon(BootstrapIcons.FILE);
		}
	}

	private StackedFontIcon createStackedFontIcon(FontIcon outer, FontIcon inner) {
		var result = new StackedFontIcon();
		result.getChildren().addAll(outer, inner);
		return result;
	}

	private TreeView<FileTreeEntry> createTreeView(String projectName, Path directoryPath) {
		var root = new TreeItem<>(new FileTreeEntry(directoryPath), new FontIcon(BootstrapIcons.APP));
		addFileTreeItems(directoryPath, root);
		root.setExpanded(true);

		var view = new TreeView<FileTreeEntry>();
		view.setRoot(root);
		view.setShowRoot(false);
		view.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		view.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			if (view.getSelectionModel().isEmpty())
				return;
			if (e.getCode().equals(KeyCode.BACK_SPACE) || e.getCode().equals(KeyCode.DELETE)) {
				try {
					var pathsToDelete = view.getSelectionModel().getSelectedItems();
					var pathsString = String.join(",", pathsToDelete.stream()
							.map(v -> v.getValue().path().toString()).toList());
					var result = EditorActions.showConfirmDialog("Are you sure?",
							"Delete files? [%s]".formatted(pathsString),
							view.getScene().getWindow());
					if (result.isEmpty() || !result.get())
						return;
					for (var pathToDelete : pathsToDelete)
						Files.delete(pathToDelete.getValue().path());
					updateTreeView();
					Toast.success("deleted %s".formatted(pathsString.toString()));
				} catch (IOException e1) {
					logger.error("{}: {}", e1.getClass().getSimpleName(), e1.getMessage(), e1);
				}
			}
		});
		view.setOnMouseClicked((e) -> {
			if (e.getClickCount() == 2)
				onPathClicked(view.getSelectionModel().getSelectedItem().getValue());
		});
		Styles.toggleStyleClass(view, Styles.DENSE);
		Styles.toggleStyleClass(view, Tweaks.EDGE_TO_EDGE);
		VBox.setVgrow(view, Priority.ALWAYS);
		return view;
	}

	private void watchForFileTreeChanges() {
		try {
			watchService = FileSystems.getDefault().newWatchService();
			fileTree.getRoot().getValue().path().register(watchService,
					StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_DELETE);
			if (watcherThread != null)
				watcherThread.interrupt();
			watcherThread = new Thread(this::watchDirectory);
			watcherThread.setName("directoryWatcher");
			watcherThread.setDaemon(true);
			watcherThread.start();

			useGitignoreMatcher.addListener((e, o, n) -> Platform.runLater(this::updateTreeView));
			useGrapheditIgnoreMatcher.addListener((e, o, n) -> Platform.runLater(this::updateTreeView));
			showHiddenFiles.addListener((e, o, n) -> Platform.runLater(this::updateTreeView));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void watchDirectory() {
		try {
			while (true) {
				var key = watchService.take();
				for (var event : key.pollEvents()) {
					var kind = event.kind();
					var eventPath = (Path) event.context();
					var fullPath = ((Path) key.watchable()).resolve(eventPath);
					Platform.runLater(() -> {
						initializeGlobMatchers();
						updateTreeView(kind, fullPath);
					});
				}
				key.reset();
			}
		} catch (InterruptedException e) {
			logger.trace(e.getMessage(), e);
		}
	}

	private void updateTreeView() {
		var newItem = new TreeItem<>(new FileTreeEntry(fileTree.getRoot().getValue().path()));
		addFileTreeItems(fileTree.getRoot().getValue().path(), newItem);
		newItem.setExpanded(true);
		fileTree.setRoot(newItem);
	}

	/**
	 * Does a full reload of the file tree. Note that this will "close" any open
	 * folders
	 * 
	 * @param eventKind
	 * @param fullPath
	 */
	private void updateTreeView(WatchEvent.Kind<?> eventKind, Path fullPath) {
		var newItem = new TreeItem<>(new FileTreeEntry(fileTree.getRoot().getValue().path()));
		addFileTreeItems(fileTree.getRoot().getValue().path(), newItem);
		newItem.setExpanded(true);
		fileTree.setRoot(newItem);
	}

	private void addFileTreeItems(Path dirPath, TreeItem<FileTreeEntry> parent) {
		try {
			for (var path : Files.newDirectoryStream(dirPath)) {
				if (!showHiddenFiles.get() && Files.isHidden(path))
					continue;
				if (useGrapheditIgnoreMatcher.get() && grapheditIgnoreMatcher.matches(path))
					continue;
				if (useGitignoreMatcher.get() && isGitignored(path))
					continue;
				var treeEntry = createTreeEntry(path);
				if (Files.isDirectory(path))
					addFileTreeItems(path, treeEntry);
				parent.getChildren().add(treeEntry);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private TreeItem<FileTreeEntry> createTreeEntry(Path path) {
		return new TreeItem<FileTreeEntry>(new FileTreeEntry(path), getPathFontIcon(path));
	}

	public void toggle() {
		setVisible(!visibleProperty().get());
	}

	private void onPathClicked(FileTreeEntry f) {
		EditorActions.openModel(f.path());
	}
}
