package dk.gtz.graphedit;

import java.util.List;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(names = { "-h", "--help" }, description = "Show this message")
    public Boolean help = false;
    @Parameter(names = { "-v", "--verbosity" }, description = "Set verbosity level")
    public String verbosity = "INFO";
    @Parameter(names = { "-P", "--plugin-dir" }, description = "Set directory to look for plugins in")
    public List<String> pluginDirs = List.of("plugins");
}

