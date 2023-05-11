package dk.gtz.graphedit.demo;

import com.beust.jcommander.Parameter;

import ch.qos.logback.classic.Level;

public class Args {
    @Parameter(names = { "-h", "--help" }, description = "Show this message")
    public Boolean help = false;
    @Parameter(names = { "-v", "--verbosity" }, description = "Set verbosity level")
    public String verbosity = "INFO";
}

