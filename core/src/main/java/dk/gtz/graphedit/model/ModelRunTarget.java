package dk.gtz.graphedit.model;

import java.util.List;
import java.util.Map;

/**
 * Runtarget datastructure. A runtarget is an abstraction over a command-line program call
 * @param name The name of the runtarget
 * @param command The command to run - must be a valid path to an executable
 * @param arguments List of command arguments
 * @param cwd Where to execute the command from
 * @param runAsShell When true, will run the command in a shell
 * @param saveBeforeRun When true, will save the project before running the runtarget
 * @param environment Mapping of environment variable assignments
 */
public record ModelRunTarget(
    String name,
    String command,
    List<String> arguments,
    String cwd,
    Boolean runAsShell,
    Boolean saveBeforeRun,
    Map<String, String> environment) {}

