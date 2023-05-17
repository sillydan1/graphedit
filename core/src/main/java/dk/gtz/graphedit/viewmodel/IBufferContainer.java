package dk.gtz.graphedit.viewmodel;

import dk.gtz.graphedit.model.Model;
import javafx.collections.ObservableMap;

public interface IBufferContainer {
    Model get(String key) throws Exception;
    void close(String key) throws Exception;
    void open(String key) throws Exception;
    void open(String key, Model model) throws Exception;
    ObservableMap<String,Model> getBuffers();
}

