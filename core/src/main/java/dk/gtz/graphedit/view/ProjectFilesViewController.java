package dk.gtz.graphedit.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.LoggerFactory;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import ch.qos.logback.classic.Logger;
import dk.gtz.graphedit.skyhook.DI;
import dk.gtz.graphedit.viewmodel.ViewModelProject;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;

public class ProjectFilesViewController {
    private static Logger logger = (Logger)LoggerFactory.getLogger(GraphEditApplication.class);

    @FXML
    public VBox root;

    @FXML
    private void initialize() {
	var openProject = DI.get(ViewModelProject.class);
	root.getChildren().add(createTreeView(openProject.name().get(), openProject.rootDirectory().get()));
    }
    
    private static String getPathLeafName(Path p) {
	var pathString = p.toString();
	return pathString.substring(pathString.lastIndexOf(File.separator)+1);
    }

    private FontIcon getPathFontIcon(Path p) {
	// TODO: Check if the path is a recognized file type and have a special icon for that
	return new FontIcon(Files.isDirectory(p) ? BootstrapIcons.FOLDER : BootstrapIcons.FILE_EARMARK);
    }

    private TreeView<String> createTreeView(String projectName, String directoryPath) {
	var root = new TreeItem<>(projectName, new FontIcon(BootstrapIcons.APP));
	addFileTreeItems(directoryPath, root);
	root.setExpanded(true);

	var view = new TreeView<String>();
	view.setRoot(root);
	view.setShowRoot(false);
	// view.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
	//	TODO: react on on-click 
	// });
	Styles.toggleStyleClass(view, Styles.DENSE);
	Styles.toggleStyleClass(view, Tweaks.EDGE_TO_EDGE);
	return view;
    }

    private void addFileTreeItems(String dirPath, TreeItem<String> parent) {
	try {
	    for(var path : Files.newDirectoryStream(Paths.get(dirPath))) {
		if(Files.isHidden(path))
		    continue; // TODO: add a toggle in the settings menu
		var subDir = new TreeItem<String>(getPathLeafName(path), getPathFontIcon(path));
		if(Files.isDirectory(path))
		    addFileTreeItems(path.toString(), subDir);
		parent.getChildren().add(subDir);
	    }
	} catch (IOException e) {
	    logger.error(e.getMessage(), e);
	}
    }
}

