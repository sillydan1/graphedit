package dk.gtz.graphedit.viewmodel;

public interface ICloseable {
    void onClose(Runnable closer);
    void close();
}

