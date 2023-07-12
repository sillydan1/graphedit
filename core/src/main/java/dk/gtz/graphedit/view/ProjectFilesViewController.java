package dk.gtz.graphedit.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.UUID;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import dk.gtz.graphedit.model.ModelEdge;
import dk.gtz.graphedit.model.ModelGraph;
import dk.gtz.graphedit.model.ModelProjectResource;
import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.skyhook.DI;
import dk.gtz.graphedit.view.util.IconUtils;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelProject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;

public class ProjectFilesViewController {
    private static record FileTreeEntry(Path path) {
	@Override
	public String toString() {
	    return path.getFileName().toString();
	}
    }

    private static Logger logger = LoggerFactory.getLogger(ProjectFilesViewController.class);
    private ViewModelProject openProject;
    private IModelSerializer serializer;
    private IBufferContainer openBuffers;
    private TreeView<FileTreeEntry> fileTree;
    private WatchService watchService;

    @FXML
    public VBox root;

    @FXML
    private void initialize() {
	openProject = DI.get(ViewModelProject.class);
	serializer = DI.get(IModelSerializer.class);
	openBuffers = DI.get(IBufferContainer.class);
	fileTree = createTreeView(openProject.name().get(), Path.of(openProject.rootDirectory().get()));
	root.getChildren().add(fileTree);
	watchForFileTreeChanges();
    }
    
    private FontIcon getPathFontIcon(Path p) {
	try {
	    if(Files.isDirectory(p))
		return new FontIcon(BootstrapIcons.FOLDER);
	    return IconUtils.getFileTypeIcon(Files.probeContentType(p));
	} catch (IOException e) {
	    logger.error(e.getMessage());
	    return new FontIcon(BootstrapIcons.FILE);
	}
    }

    private TreeView<FileTreeEntry> createTreeView(String projectName, Path directoryPath) {
	var root = new TreeItem<>(new FileTreeEntry(directoryPath), new FontIcon(BootstrapIcons.APP));
	addFileTreeItems(directoryPath, root);
	root.setExpanded(true);

	var view = new TreeView<FileTreeEntry>();
	view.setRoot(root);
	view.setShowRoot(false);
	view.setOnMouseClicked((e) -> {
	    if(e.getClickCount() == 2)
		onPathClicked(view.getSelectionModel().getSelectedItem().getValue());
	});
	Styles.toggleStyleClass(view, Styles.DENSE);
	Styles.toggleStyleClass(view, Tweaks.EDGE_TO_EDGE);
	return view;
    }

    private void watchForFileTreeChanges() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
            fileTree.getRoot().getValue().path().register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
	    var watcherThread = new Thread(this::watchDirectory);
	    watcherThread.setDaemon(true);
	    watcherThread.start();
        } catch (IOException e) {
	    logger.error(e.getMessage(), e);
        }
    }

    private void watchDirectory() {
        try {
            while(true) {
                var key = watchService.take();
                for(var event : key.pollEvents()) {
                    var kind = event.kind();
                    var eventPath = (Path) event.context();
                    var fullPath = ((Path) key.watchable()).resolve(eventPath);
                    Platform.runLater(() -> updateTreeView(kind, fullPath));
                }
                key.reset();
            }
        } catch (InterruptedException e) {
	    logger.trace(e.getMessage(), e);
        }
    }

    private void updateTreeView(WatchEvent.Kind<?> eventKind, Path fullPath) {
	// TODO: This currently does a full re-load. It shouldn't.
        var newItem = new TreeItem<>(new FileTreeEntry(fileTree.getRoot().getValue().path()));
	addFileTreeItems(fileTree.getRoot().getValue().path(), newItem);
	newItem.setExpanded(true);
	fileTree.setRoot(newItem);
    }

    private void addFileTreeItems(Path dirPath, TreeItem<FileTreeEntry> parent) {
	try {
	    for(var path : Files.newDirectoryStream(dirPath)) {
		if(Files.isHidden(path))
		    continue; // TODO: add a toggle in the settings menu, also maybe respect .gitignore/.graphedit.ignore idk.
		var subDir = new TreeItem<FileTreeEntry>(new FileTreeEntry(path), getPathFontIcon(path));
		if(Files.isDirectory(path))
		    addFileTreeItems(path, subDir);
		parent.getChildren().add(subDir);
	    }
	} catch (IOException e) {
	    logger.error(e.getMessage(), e);
	}
    }

    public void toggle() {
	root.setVisible(!root.visibleProperty().get());
    }

    public void createNewModelFile() {
	var selected = fileTree.getSelectionModel().getSelectedItem();
	if(selected == null) {
	    // TODO: Just default to the root-dir. This is good enough for now
	    logger.warn("cannot create new model file, since no destination folder is selected");
	    return;
	}
	var basePath = selected.getValue().path().toString();
	var dialog = new TextInputDialog();
	dialog.setTitle("new model file");
	dialog.setHeaderText(selected.getValue().path().toString());
	dialog.setContentText("new filename:");
	dialog.initOwner(root.getScene().getWindow()); // TODO: experiment with null window
	var fileName = dialog.showAndWait();
	if(fileName.isPresent()) {
	    try {
		logger.trace("trying to create file {}/{}", basePath, fileName.get());
		var newfile = new File("%s/%s".formatted(basePath, fileName.get()));
		if(!newfile.createNewFile()) {
		    logger.error("file already exists");
		    return;
		}
		// TODO: This should be created with a factory
		// TODO: Saving a specific model file should be extracted into a util function, since it is very useful and is already being reused (DRY)
		var exampleVertices = new HashMap<UUID,ModelVertex>();
		var exampleEdges = new HashMap<UUID,ModelEdge>();
		var newModel = new ModelProjectResource(new HashMap<>(), new ModelGraph("", exampleVertices, exampleEdges));
		var serializedModel = DI.get(IModelSerializer.class).serialize(newModel);
		Files.write(Paths.get(newfile.getCanonicalPath()), serializedModel.getBytes());
		logger.info("file created");
	    } catch (Exception e) {
		logger.error(e.getMessage());
	    }
	}
    }

    private void onPathClicked(FileTreeEntry f) {
	try {
	    var p = f.path();
	    if(!Files.isRegularFile(p)) {
		logger.trace("not a file. ignoring click. {}", p.toString());
		return;
	    }
	    logger.debug("opening file {}", p.toString());
	    var fileType = Files.probeContentType(p);
	    if(fileType == null) {
		logger.error("unknown filetype, cannot open");
		return;
	    }
	    if(!serializer.getSupportedContentTypes().contains(fileType)) {
		logger.error("cannot open unsupported filetype '{}'", fileType);
		return;
	    }
	    openBuffers.open(p.toString());
	} catch (Exception e) {
	    logger.error(e.getMessage());
	}
    }
}

