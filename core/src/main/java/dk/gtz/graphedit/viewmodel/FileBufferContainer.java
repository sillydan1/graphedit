package dk.gtz.graphedit.viewmodel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.exceptions.NotFoundException;
import dk.gtz.graphedit.exceptions.SerializationException;
import dk.gtz.graphedit.model.Model;
import dk.gtz.graphedit.serialization.IModelSerializer;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;

public class FileBufferContainer implements IBufferContainer {
    private final Logger logger = LoggerFactory.getLogger(FileBufferContainer.class);
    private final SimpleMapProperty<String, Model> openBuffers;
    private final IModelSerializer serializer;

    public FileBufferContainer(IModelSerializer serializer) {
        openBuffers = new SimpleMapProperty<>();
        this.serializer = serializer;
    }

    @Override
    public MapProperty<String, Model> getBuffers() {
        return openBuffers;
    }

    @Override
    public Model get(String filename) throws NotFoundException {
        if(!openBuffers.containsKey(filename))
            throw new NotFoundException("no such buffer: %s".formatted(filename));
        return openBuffers.get(filename);
    }

    @Override
    public void close(String filename) {
        if(openBuffers.containsKey(filename))
            openBuffers.remove(filename);
    }

    @Override
    public void open(String filename) {
        try {
            if(openBuffers.containsKey(filename))
                return; // TODO: trigger a file focus event (focus feature not implemented yet)
            var b = new StringBuilder();
            var f = new File(filename);
            var s = new Scanner(f);
            while(s.hasNextLine())
                b.append(s.nextLine());
            s.close();
            var newModel = serializer.deserialize(b.toString());
            openBuffers.put(filename, newModel);
        } catch (SerializationException | FileNotFoundException e) {
            logger.error(e.getMessage());
        }
    }
}

