package dk.gtz.graphedit.viewmodel;

import dk.gtz.graphedit.model.ModelProjectResource;
import javafx.collections.ObservableMap;

public interface IBufferContainer {
    ModelProjectResource get(String key) throws Exception;
    void close(String key) throws Exception;
    void open(String key) throws Exception;
    void open(String key, ModelProjectResource model) throws Exception;
    ObservableMap<String,ModelProjectResource> getBuffers();
}

