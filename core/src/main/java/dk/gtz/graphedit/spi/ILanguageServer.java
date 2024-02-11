package dk.gtz.graphedit.spi;

import java.io.File;
import java.util.Collection;

import dk.gtz.graphedit.model.ModelLint;
import dk.gtz.graphedit.model.lsp.ModelLanguageServerProgress;
import dk.gtz.graphedit.model.lsp.ModelNotification;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.yalibs.yafunc.IRunnable1;

/**
 * Interface and specification of a language server.
 *
 * Language servers provide the language smarts that help developers create better, safer and valid models.
 * A server provides diagnostics, lints, optimizations, suggestions and notifications.
 */
public interface ILanguageServer {
    /**
     * Get the name of the language that the server supports.
     * Expected to match what {@link ISyntaxFactory#getSyntaxName} provides
     * @return A string with the name of the supported language.
     */
    String getLanguageName();

    /**
     * Get the name of the language server (not to be confused with {@link getLanguageName})
     * @return A string representing the name of the language server
     */
    String getServerName();

    /**
     * Get the version string of the language server.
     * i.e. "v1.0.0"
     * @return A string representing the version of the language server
     */
    String getServerVersion();

    /**
     * Function to initialize (not start) the language server
     * This will be called right before any callback functions are added
     * @param projectFile The Graphedit project file. Note that this can also just be a temporary directory
     * @param bufferContainer The collection of buffers, likely (but not guaranteed) to be empty at initialization
     */
    void initialize(File projectFile, IBufferContainer bufferContainer);

    /**
     * Start the language server.
     * This will be called from a seperate thread and will be interrupted via {@link Thread#interrupt} when it's time to close.
     * This means that this function should be a blocking call
     */
    void start();

    /**
     * Get all the diagnostics that are currently known to the server.
     * @return A collection of lints.
     */
    Collection<ModelLint> getDiagnostics();

    /**
     * Add a callback function to call when diagnostics are updated.
     * Note that the callback expects a complete collection of lints.
     * @param callback The function to call when the diagnostics collections change.
     */
    void addDiagnosticsCallback(IRunnable1<Collection<ModelLint>> callback);

    /**
     * Add a callback function to call when a notification is presented.
     * @param callback The function to call when a notification should be shown.
     */
    void addNotificationCallback(IRunnable1<ModelNotification> callback);

    /**
     * Add a callback function to call when language server progress notification happens.
     * @param callback The function to call when a progress notification should be shown.
     */
    void addProgressCallback(IRunnable1<ModelLanguageServerProgress> callback);
}
