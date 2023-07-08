package dk.gtz.graphedit.view;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.LoggerFactory;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import ch.qos.logback.classic.Logger;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.skyhook.DI;
import dk.gtz.graphedit.view.util.IconUtils;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelProject;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import javafx.fxml.FXML;
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

    private static Logger logger = (Logger)LoggerFactory.getLogger(GraphEditApplication.class);
    private ViewModelProject openProject;
    private IModelSerializer serializer;
    private IBufferContainer openBuffers;

    @FXML
    public VBox root;

    @FXML
    private void initialize() {
	openProject = DI.get(ViewModelProject.class);
	serializer = DI.get(IModelSerializer.class);
	openBuffers = DI.get(IBufferContainer.class);
	root.getChildren().add(createTreeView(openProject.name().get(), openProject.rootDirectory().get()));
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

    private TreeView<FileTreeEntry> createTreeView(String projectName, String directoryPath) {
	var root = new TreeItem<>(new FileTreeEntry(Path.of(directoryPath)), new FontIcon(BootstrapIcons.APP));
	addFileTreeItems(directoryPath, root);
	root.setExpanded(true);

	var view = new TreeView<FileTreeEntry>();
	view.setRoot(root);
	view.setShowRoot(false);
	view.setOnMouseClicked((e) -> {
	    if(e.getClickCount() == 2)
		onFileClicked(view.getSelectionModel().getSelectedItem().getValue().path());
	});
	Styles.toggleStyleClass(view, Styles.DENSE);
	Styles.toggleStyleClass(view, Tweaks.EDGE_TO_EDGE);
	return view;
    }

    private void addFileTreeItems(String dirPath, TreeItem<FileTreeEntry> parent) {
	try {
	    for(var path : Files.newDirectoryStream(Paths.get(dirPath))) {
		if(Files.isHidden(path))
		    continue; // TODO: add a toggle in the settings menu, also maybe respect .gitignore/.graphedit.ignore idk.
		var subDir = new TreeItem<FileTreeEntry>(new FileTreeEntry(path), getPathFontIcon(path));
		if(Files.isDirectory(path))
		    addFileTreeItems(path.toString(), subDir);
		parent.getChildren().add(subDir);
	    }
	} catch (IOException e) {
	    logger.error(e.getMessage(), e);
	}
    }

    public void toggle() {
	root.setVisible(!root.visibleProperty().get());
    }

    // TODO: move this into a FileUtils
    private String readFileContent(Path p) throws IOException {
	var myReader = new Scanner(p.toFile());
	var sb = new StringBuilder();
	while (myReader.hasNextLine())
	    sb.append(myReader.nextLine());
	myReader.close();
	return sb.toString();
    }

    private void onFileClicked(Path p) {
	try {
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
	    if(openBuffers.contains(p.toString()))
		// TODO: focus on the tab
		return;
	    var model = serializer.deserialize(readFileContent(p));
	    openBuffers.open(p.toString(), new ViewModelProjectResource(model));
	} catch (Exception e) {
	    logger.error(e.getMessage());
	}
    }
}

