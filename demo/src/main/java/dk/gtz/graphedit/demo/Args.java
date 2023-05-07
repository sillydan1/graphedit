package dk.gtz.graphedit.demo;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(names = { "-h", "--help" }, description = "Show this message")
    public Boolean help = false;

    // not very exciting, is it?
}

