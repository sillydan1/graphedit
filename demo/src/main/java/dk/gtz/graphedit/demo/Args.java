package dk.gtz.graphedit.demo;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(names = { "-h", "--help" }, description = "Show this message")
    public Boolean help = false;
    @Parameter(names = { "-v", "--verbosity" }, description = "Set verbosity level")
    public String verbosity = "INFO";
    @Parameter(names = { "-P", "--skip-preloader" }, description = "Skip the preloader and open the most recent project")
    public Boolean skipPreloader = false;
}

