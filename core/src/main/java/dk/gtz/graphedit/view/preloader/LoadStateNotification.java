package dk.gtz.graphedit.view.preloader;

import javafx.application.Preloader.PreloaderNotification;

public class LoadStateNotification implements PreloaderNotification {
    private final String loadStateMessage;

    public LoadStateNotification(String loadStateMessage) {
        this.loadStateMessage = loadStateMessage;
    }

    public String getLoadStateMessage() {
        return loadStateMessage;
    }
}

