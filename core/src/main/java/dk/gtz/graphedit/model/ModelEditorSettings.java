package dk.gtz.graphedit.model;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import dk.gtz.graphedit.util.PlatformUtils;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;

/**
 * General editor settings model object containing a users' prefered theme, recent projects and other edior-wide preferences and settings.
 * This is meant to be serialized and deserialized to/from disk
 */
public record ModelEditorSettings(double gridSizeX, double gridSizeY, boolean gridSnap, boolean useLightTheme, boolean autoOpenLastProject, boolean showInspectorPane, boolean showInfoToasts, boolean showWarnToasts, boolean showErrorToasts, boolean showTraceToasts, String lastOpenedProject, List<String> recentProjects) {
    /**
     * Creates a ModelEditorSettings instance with default values.
     */
    public ModelEditorSettings() {
        this(20.0d, 20.0d, true, false, true, false, true, true, true, false, "", new ArrayList<>());
    }

    /**
     * Creates a ModelEditorSettings instance based on the associated ViewModel
     * @param viewmodel The viewmodel to base the new instance value off of
     */
    public ModelEditorSettings(ViewModelEditorSettings viewmodel) {
        this(viewmodel.gridSizeX().get(),
            viewmodel.gridSizeY().get(),
            viewmodel.gridSnap().get(),
            viewmodel.useLightTheme().get(),
            viewmodel.autoOpenLastProject().get(),
            viewmodel.showInspectorPane().get(),
            viewmodel.showInfoToasts().get(),
            viewmodel.showWarnToasts().get(),
            viewmodel.showErrorToasts().get(),
            viewmodel.showTraceToasts().get(),
            viewmodel.lastOpenedProject().get(),
            new ArrayList<String>(viewmodel.recentProjects().get()));
    }

    /**
     * Get the file of the editor settings.
     * Note that the filepath may be different depending on the operating system and $HOME variable
     * @return The OS-specific file path to editor settings
     */
    public static Path getEditorSettingsFile() {
        if(PlatformUtils.isWindows())
            return Path.of(System.getenv("AppData") + File.separator + "graphedit-settings.json");
        var userHome = System.getProperty("user.home");
        if(PlatformUtils.isMac())
            userHome += "/Library/Application Support/Graphedit/";
        else
            userHome += "/.local/graphedit/";
        return Path.of(userHome + "graphedit-settings.json");
    }
}

