package dk.gtz.graphedit;

import java.io.File;
import java.util.List;

import com.beust.jcommander.Parameter;

import dk.gtz.graphedit.util.PlatformUtils;

public class Args {
    @Parameter(names = { "-h", "--help" }, description = "Show this message")
    public Boolean help = false;
    @Parameter(names = { "-v", "--verbosity" }, description = "Set verbosity level")
    public String verbosity = "INFO";
    @Parameter(names = { "-P", "--plugin-dir" }, description = "Set directory to look for plugins in")
    public List<String> pluginDirs = List.of("plugins", String.join(File.separator, configDir(), "plugins"));

    private static String configDir() {
        if(PlatformUtils.isWindows())
            return String.join(File.separator, System.getenv("AppData"), "graphedit").toString();
        var userHome = System.getProperty("user.home");
        if(PlatformUtils.isMac())
            return String.join(File.separator, userHome, "Library", "Application Support", "Graphedit");
        return String.join(File.separator, userHome, ".local", "graphedit");
    }
}

