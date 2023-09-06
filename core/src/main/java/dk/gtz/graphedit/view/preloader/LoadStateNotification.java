package dk.gtz.graphedit.view.preloader;

import javafx.application.Preloader.PreloaderNotification;

/**
 * Notification about the project load state sent from graphedit to the preloader.
 *
 * Useful for displaying the status and progress of the load process.
 */
public class LoadStateNotification implements PreloaderNotification {
    private final String loadStateMessage;

    public LoadStateNotification(String loadStateMessage) {
        this.loadStateMessage = loadStateMessage;
    }

    public String getLoadStateMessage() {
        return loadStateMessage;
    }
}

