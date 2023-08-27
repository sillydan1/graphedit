package dk.gtz.graphedit.model;

import java.io.File;
import java.nio.file.Path;

import dk.gtz.graphedit.view.util.PlatformUtils;

public record ModelEditorSettings(double gridSizeX, double gridSizeY, boolean gridSnap, boolean useLightTheme, String lastOpenedProject) {

    /**
     * Creates a ModelEditorSettings instance with default values.
     */
    public ModelEditorSettings() {
        this(20.0d, 20.0d, true, false, "");
    }

    public static Path getEditorSettingsFile() {
        if(PlatformUtils.isWindows())
            return Path.of(System.getenv("AppData") + File.separator + "graphedit-settings.json");
        var workingDirectory = System.getProperty("user.home");
        if(PlatformUtils.isMac())
            workingDirectory += "/Library/Application Support/Graphedit/";
        else
            workingDirectory += "/local/.graphedit/";
        return Path.of(workingDirectory + "graphedit-settings.json");
    }
}

