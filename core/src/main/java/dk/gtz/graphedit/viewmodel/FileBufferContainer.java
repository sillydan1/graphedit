package dk.gtz.graphedit.viewmodel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.exceptions.SerializationException;
import dk.gtz.graphedit.serialization.IModelSerializer;
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
            var f = new File(filename);
            var fp = f.getAbsolutePath();
            if(openBuffers.containsKey(fp))
                return; // TODO: trigger a file focus event (focus feature not implemented yet)
            var b = new StringBuilder();
            var s = new Scanner(f);
            while(s.hasNextLine())
                b.append(s.nextLine());
            s.close();
            var newModel = serializer.deserialize(b.toString());
            open(fp, new ViewModelProjectResource(newModel));
        } catch (SerializationException | FileNotFoundException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void open(String filename, ViewModelProjectResource model) {
        Platform.runLater(() -> openBuffers.put(filename, model));
    }
}

