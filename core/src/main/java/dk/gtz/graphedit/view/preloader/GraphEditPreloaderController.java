package dk.gtz.graphedit.view.preloader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.model.ModelProject;
import dk.gtz.graphedit.serialization.IModelSerializer;

import dk.gtz.graphedit.tool.EditorActions;
import dk.gtz.graphedit.view.util.PlatformUtils;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.yalibs.yadi.DI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * javafx controller for the graphedt preloader view
 */
public class GraphEditPreloaderController {
    private static Logger logger = LoggerFactory.getLogger(GraphEditPreloaderController.class);

    private static record ProjectData(String name, String filePath) {}

    @FXML
    private BorderPane root;
    @FXML
    private Button openNewProjectButton;
    @FXML
    private Button newProjectButton;
    @FXML
    private VBox projectContainer;
    @FXML
    private HBox settingsContainer;
    @FXML
    private HBox bottomContainer;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label statusLabel;
    @FXML
    private ImageView logo;
    private ViewModelEditorSettings editorSettings;
    private boolean isStarted;

    public GraphEditPreloaderController() {
        this.isStarted = false;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void start() {
        isStarted = true;
        editorSettings = DI.get(ViewModelEditorSettings.class);
        initializeOpenNewProjectButton();
        initializeProjectContainer();
    }

    private void initializeOpenNewProjectButton() {
        openNewProjectButton.setGraphic(new FontIcon(BootstrapIcons.SQUARE));
        openNewProjectButton.setText("open project...");
        openNewProjectButton.setOnAction((e) -> {
            var file = EditorActions.openProjectPicker(root.getScene().getWindow());
            if(file.isPresent())
                EditorActions.openProject(file.get());
        });

        newProjectButton.setGraphic(new FontIcon(BootstrapIcons.PLUS_SQUARE_DOTTED));
        newProjectButton.setText("create new project");
        newProjectButton.setOnAction(e -> {
            var file = EditorActions.saveProjectPicker(root.getScene().getWindow());
            if(!file.isPresent())
                return;
            // TODO: project data inspector / editor so people can change the project name later
            var modelProject = new ModelProject(PlatformUtils.removeFileExtension(file.get().getName()));
            EditorActions.saveProject(modelProject, Path.of(file.get().getAbsolutePath()));
            EditorActions.openProject(file.get());
        });
    }

    private void initializeProjectContainer() {
        var serializer = DI.get(IModelSerializer.class);
        var projectData = new ArrayList<ProjectData>();
        for(var projectFilePath : editorSettings.recentProjects()) {
            try {
                var projectSettings = serializer.deserializeProject(Path.of(projectFilePath).toFile());
                projectData.add(new ProjectData(projectSettings.name(), projectFilePath));
            } catch(IOException e) {
                logger.warn("could not load project %s".formatted(projectFilePath), e);
            }
        }

        projectContainer.getChildren().clear();
        for(var project : projectData) {
            var projectBox = new HBox(new FontIcon(BootstrapIcons.FOLDER), new Label(project.name()));
            projectBox.setOnMouseClicked((e) -> EditorActions.openProject(Path.of(project.filePath).toFile()));
            projectContainer.getChildren().add(projectBox);
        }
    }

    public void handleNotification(LoadStateNotification notification) {
        logger.trace(notification.getLoadStateMessage());
        statusLabel.setText(notification.getLoadStateMessage());
    }
}

