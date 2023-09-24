package dk.gtz.graphedit.viewmodel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.exceptions.SerializationException;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.yalibs.yadi.DI;
import dk.yalibs.yaerrors.NotFoundException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class FileBufferContainer implements IBufferContainer {
    private final Logger logger = LoggerFactory.getLogger(FileBufferContainer.class);
    private final ObservableMap<String, ViewModelProjectResource> openBuffers;
    private final IModelSerializer serializer;

    public FileBufferContainer(IModelSerializer serializer) {
        openBuffers = FXCollections.observableHashMap(); 
        this.serializer = serializer;
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
            var newModel = serializer.deserialize(b.toString());
            open(filename, new ViewModelProjectResource(newModel));
        } catch (SerializationException | FileNotFoundException e) {
            logger.error("not a proper model file {}", filename, e);
            logger.trace(e.getMessage());
        }
    }

    @Override
    public void open(String filename, ViewModelProjectResource model) {
        Platform.runLater(() -> openBuffers.put(filename, model));
    }
}

