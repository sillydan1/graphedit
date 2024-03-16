package dk.gtz.graphedit.viewmodel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.exceptions.SerializationException;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.util.MetadataUtils;
import dk.yalibs.yadi.DI;
import dk.yalibs.yaerrors.NotFoundException;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * Buffer container implementation using filepaths as keys.
 */
public class FileBufferContainer implements IBufferContainer {
    private final Logger logger = LoggerFactory.getLogger(FileBufferContainer.class);
    private final ObservableMap<String, ViewModelProjectResource> openBuffers;
    private final IModelSerializer serializer;
    private final ObjectProperty<ViewModelProjectResource> focusedBuffer;

    /**
     * Constructs a new filepath keyed buffer container.
     * @param serializer The serializer to use when deserializing the buffers
     */
    public FileBufferContainer(IModelSerializer serializer) {
        openBuffers = FXCollections.observableHashMap(); 
        this.serializer = serializer;
        this.focusedBuffer = new SimpleObjectProperty<>();
    }

    @Override
    public ObservableMap<String, ViewModelProjectResource> getBuffers() {
        return openBuffers;
    }

    @Override
    public ViewModelProjectResource get(String filename) throws NotFoundException {
        if(!contains(filename))
            throw new NotFoundException("no such buffer: %s".formatted(filename));
        return openBuffers.get(filename);
    }

    @Override
    public boolean contains(String filename) {
        return openBuffers.containsKey(filename);
    }

    @Override
    public void close(String filename) {
        Platform.runLater(() -> {
            if(openBuffers.containsKey(filename))
                openBuffers.remove(filename);
        });
    }

    @Override
    public void open(String filename) {
        try {
            var baseDir = DI.get(ViewModelProject.class).rootDirectory().getValueSafe();
            var f = new File(baseDir + File.separator + filename);
            if(openBuffers.containsKey(filename)) {
                openBuffers.get(filename).focus();
                return;
            }
            var b = new StringBuilder();
            var s = new Scanner(f);
            while(s.hasNextLine())
                b.append(s.nextLine());
            s.close();
            var newModel = serializer.deserializeProjectResource(b.toString());
            open(filename, new ViewModelProjectResource(newModel, MetadataUtils.getSyntaxFactory(newModel.metadata())));
        } catch (SerializationException | FileNotFoundException e) {
            logger.error("not a proper model file {}", filename, e);
            logger.trace(e.getMessage());
        }
    }

    @Override
    public void open(String filename, ViewModelProjectResource model) {
        Platform.runLater(() -> openBuffers.put(filename, model));
    }

    @Override
    public ObjectProperty<ViewModelProjectResource> getCurrentlyFocusedBuffer() {
        logger.trace("getting focused buffer: {}", focusedBuffer.get());
        return focusedBuffer;
    }
}
