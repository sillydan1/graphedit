package dk.gtz.graphedit.tool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.controls.ModalPane;
import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.Styles;
import dk.gtz.graphedit.BuildConfig;
import dk.gtz.graphedit.exceptions.SerializationException;
import dk.gtz.graphedit.logging.Toast;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.view.EditorController;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.gtz.graphedit.viewmodel.ViewModelProject;
import dk.gtz.graphedit.viewmodel.ViewModelRunTarget;
import dk.yalibs.yadi.DI;
import dk.yalibs.yafunc.IFunction2;
import dk.yalibs.yastreamgobbler.StreamGobbler;
import dk.yalibs.yaundo.IUndoSystem;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

// TODO: write javadocs on all of these
public class EditorActions {
    private static final Logger logger = LoggerFactory.getLogger(EditorActions.class);

    public static void quit() {
        var alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Save and Exit?");
        alert.setHeaderText("Save your changes before you exit?");
        var yesBtn = new ButtonType("Save and exit", ButtonData.YES);
        var noBtn = new ButtonType("Exit without saving", ButtonData.NO);
        var modalPane = DI.get(ModalPane.class); // TODO: This is a hack, we should decide on a unified modal design language
        alert.getButtonTypes().setAll(yesBtn, noBtn);
        alert.initOwner(modalPane.getScene().getWindow());
        var result = alert.showAndWait();
        if(result.isEmpty()) {
            logger.warn("cancelling quit action");
            return;
        }
        if(result.get().getButtonData().equals(ButtonData.NO)) {
            Platform.exit();
            return;
        }
        EditorActions.save();
        Platform.exit();
    }

    public static void save() {
        var serializer = DI.get(IModelSerializer.class);
        var buffers = DI.get(IBufferContainer.class).getBuffers().entrySet();
        logger.trace("save starting");
        buffers.parallelStream().forEach((buffer) -> {
            try {
                var filePath = buffer.getKey();
                logger.trace("saving file {}", filePath);
                var model = buffer.getValue().toModel();
                var serializedModel = serializer.serialize(model);
                var p = Paths.get(filePath);
                Files.createDirectories(p.getParent());
                Files.write(p, serializedModel.getBytes());
            } catch (SerializationException e) {
                logger.error("failed to serialize model '{}' reason: {}", buffer.getKey(), e.getMessage());
            } catch (IOException e) {
                logger.error("failed to save file '{}' reason: {}", buffer.getKey(), e.getMessage());
            }
        });
        Toast.success("save complete");
        logger.trace("save complete");
    }

    public static void toggleTheme() {
        var useLightTheme = DI.get(ViewModelEditorSettings.class).useLightTheme();
        useLightTheme.set(!useLightTheme.get());
    }

    public static void openSettingsEditor() {
        openModal("SettingsEditor.fxml");
    }

    public static void openRunTargetsEditor() {
        openModal("RunTargetsEditor.fxml");
    }

    public static void openSearchPane() {
        openModal("SearchPane.fxml");
    }

    public static void openAboutPane() {
        IFunction2<Text,String,String> createText = (String text, String style) -> {
            var returnValue = new Text(text);
            returnValue.getStyleClass().add(style);
            return returnValue;
        };
        var aboutNode = new VBox(
            createText.run(BuildConfig.APP_NAME, Styles.TITLE_1),
            createText.run(BuildConfig.APP_VERSION, Styles.TITLE_3),
            new Text("TODO: better description"));
        aboutNode.setMinSize(450, 450);
        aboutNode.setMaxSize(450, 450);
        aboutNode.getStyleClass().add(Styles.BG_DEFAULT);
        openModal(aboutNode);
    }

    public static void openModal(Node node) {
        var modalPane = DI.get(ModalPane.class);
        modalPane.show(node);
        node.requestFocus();
    }

    public static void openModal(String fxmlFile) {
        try {
            var loader = new FXMLLoader(EditorController.class.getResource(fxmlFile));
            var content = (Node)loader.load();
            openModal(content);
        } catch(IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void undo() {
        DI.get(IUndoSystem.class).undo();
    }

    public static void redo() {
        DI.get(IUndoSystem.class).redo();
    }

    public static void executeRunTarget(ViewModelRunTarget selectedRunTarget) {
        // NOTE: Does not change the MenuItem labels or anything.
        // NOTE: This is a blocking call.
        try {
            var pb = new ProcessBuilder();
            if(selectedRunTarget.runAsShell().get()) {
                var sb = new StringBuilder(selectedRunTarget.command().get());
                for(var argument : selectedRunTarget.arguments())
                    sb.append(" ").append(argument.getValueSafe());
                pb.command("/bin/sh", "-c", sb.toString());
            } else {
                pb.command(selectedRunTarget.command().get());
                for(var argument : selectedRunTarget.arguments())
                    pb.command().add(argument.get());
            }
            var env = pb.environment();
            var project = DI.get(ViewModelProject.class);
            env.put("PROJECT_NAME", project.name().getValueSafe());
            env.put("PROJECT_DIR", project.rootDirectory().getValueSafe());
            for(var e : selectedRunTarget.environment().entrySet())
                env.put(e.getKey().get(), e.getValue().get());
            if(!selectedRunTarget.currentWorkingDirectory().get().isEmpty())
                pb.directory(new File(selectedRunTarget.currentWorkingDirectory().get()));
            pb.redirectErrorStream(true);
            var p = pb.start();
            var outputGobbler = new StreamGobbler(p.getInputStream(), logger::info);
            new Thread(outputGobbler).start();
            p.waitFor();
        } catch(InterruptedException e) {
            logger.warn("runtarget was interrupted", e);
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            logger.trace("runtarget finished");
        }
    }
}

