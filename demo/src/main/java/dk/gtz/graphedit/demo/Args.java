package dk.gtz.graphedit.demo;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(names = { "-h", "--help" }, description = "Show this message")
    private Boolean help = false;
	@Parameter(names = { "-v", "--verbosity" }, description = "Level of verbosity")
    private Integer verbosity = 1;

	public Integer getVerbosity() {
		return verbosity;
	}

    public Boolean getHelp() {
		return help;
	}
}

