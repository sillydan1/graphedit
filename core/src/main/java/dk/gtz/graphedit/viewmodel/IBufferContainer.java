package dk.gtz.graphedit.viewmodel;

import dk.gtz.graphedit.model.Model;

public interface IBufferContainer {
    Model get(String key) throws Exception;
    void close(String key) throws Exception;
    void open(String key) throws Exception;
}

