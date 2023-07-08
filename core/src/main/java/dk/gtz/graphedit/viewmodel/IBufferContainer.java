package dk.gtz.graphedit.viewmodel;

import javafx.collections.ObservableMap;

public interface IBufferContainer {
    ViewModelProjectResource get(String key) throws Exception;
    void close(String key);
    void open(String key) throws Exception;
    void open(String key, ViewModelProjectResource model) throws Exception;
    boolean contains(String key);
    ObservableMap<String,ViewModelProjectResource> getBuffers();
}

