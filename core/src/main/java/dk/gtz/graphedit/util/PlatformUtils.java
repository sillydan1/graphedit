package dk.gtz.graphedit.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.viewmodel.ViewModelProject;
import dk.yalibs.yadi.DI;
import dk.yalibs.yastreamgobbler.StreamGobbler;

/**
 * General utilities relating to the operating system
 */
public class PlatformUtils {
    private static final Logger logger = LoggerFactory.getLogger(PlatformUtils.class);

    private PlatformUtils() {}

    /**
     * Check if the menu bar can be delegated to the OS to handle.
     * @return {@code true} if the OS supports system menu bars
     */
    public static boolean isSystemMenuBarSupported() {
	if(isWindows())
	    return false;
	if(isMac())
	    return true;
	if(isUnix())
	    return isGtk();
	return false;
    }

    /**
     * Check if the current running platform is Microsoft Windows
     * @return {@code true} if current running platform is Microsoft Windows
     */
    public static boolean isWindows() {
	return atlantafx.base.util.PlatformUtils.isWindows();
    }

    /**
     * Check if the current running platform is Apple OSX
     * @return {@code true} if current running platform is Apple OSX
     */
    public static boolean isMac() {
	return atlantafx.base.util.PlatformUtils.isMac();
    }

    /**
     * Check if the current running platform is a UNIX-like OS - think GNU/Linux
     * @return {@code true} if current running platform is a UNIX-like OS
     */
    public static boolean isUnix() {
	return atlantafx.base.util.PlatformUtils.isUnix();
    }

    /**
     * Check if the current running platform is running a GTK-based desktop environment - think the GNOME desktop environment
     * @return {@code true} if the desktop environment is GTK-based
     */
    public static boolean isGtk() {
	var platform = System.getProperty("javafx.platform");
	return platform != null && platform.equals("gtk");
    }

    /**
     * Check if a command-line program is installed on the current running platform
     *
     * Note that this may not work on Microsoft Windows
     * @param executableName The executable to check for
     * @return true if the program is discoverable through the PATH variable, false otherwise
     */
    public static boolean isProgramInstalled(String executableName) {
	var pathVariable = System.getenv("PATH");
	var pathDirectories = pathVariable.split(File.pathSeparator);
	for (var directory : pathDirectories) {
	    var executableFile = new File(directory, executableName);
	    if (executableFile.exists() && !executableFile.isDirectory() && Files.isExecutable(executableFile.toPath()))
		return true;
	}
	return false;
    }

    /**
     * Remove the file-extension part of a filename string.
     *
     * Example: {@code "myfile.txt"} -> {@code "myfile"}
     *
     * Note that this will only remove the last file-extension, so if you have multiple, you have to call the function multiple times.
     *
     * Example: {@code "myfile.ignore.txt"} -> {@code "myfile.ignore"}
     * @param fname the filename to modify
     * @return the same filename as provided, but with the file extension removed. If there was no extension, simply return the same filename
     */
    public static String removeFileExtension(String fname) {
	var pos = fname.lastIndexOf('.');
	if(pos > -1)
	    return fname.substring(0, pos);
	return fname;
    }

    /**
     * Launch a new subprocess and wait for it to complete whilst logging the stout to TRACE-level and stderr to ERROR-level logging.
     * cwd is the root directory of the currently open project
     * Note that this will block the current thread of execution until the command is completed.
     * @param command The command to launch the subprocess with
     * @param arguments The command-line arguments to provide the subprocess with
     */
    public static void launchProgram(String command, List<String> arguments) {
	launchProgram(command, DI.get(ViewModelProject.class).rootDirectory().get(), arguments);
    }

    /**
     * Launch a new subprocess and wait for it to complete whilst logging the stout to TRACE-level and stderr to ERROR-level logging.
     * cwd is the root directory of the currently open project
     * Note that this will block the current thread of execution until the command is completed.
     * @param command The command to launch the subprocess with
     * @param arguments The command-line arguments to provide the subprocess with
     */
    public static void launchProgram(String command, String... arguments) {
	launchProgram(command, List.of(arguments));
    }

    /**
     * Launch a new subprocess and wait for it to complete whilst logging the stout to TRACE-level and stderr to ERROR-level logging.
     *
     * Note that this will block the current thread of execution until the command is completed.
     * @param command The command to launch the subprocess with
     * @param workingDir The working directory to launch the subprocess in
     * @param arguments The command-line arguments to provide the subprocess with
     */
    public static void launchProgram(String command, String workingDir, List<String> arguments) {
	Process p = null;
	try {
	    var pb = new ProcessBuilder();
	    pb.command(command);
	    pb.directory(Path.of(workingDir).toFile());
	    for(var argument : arguments)
		pb.command().add(argument);
	    pb.redirectErrorStream(true);
	    p = pb.start();
	    new Thread(new StreamGobbler(p.getInputStream(), logger::trace)).start();
	    new Thread(new StreamGobbler(p.getErrorStream(), logger::error)).start();
	    Runtime.getRuntime().addShutdownHook(new Thread(p::destroy));
	    p.waitFor();
	    var exitCode = p.exitValue();
	    logger.info("process exited with code: {}", exitCode);
	} catch(InterruptedException e) {
	    logger.warn("<{}> process was interrupted", command);
	} catch(Exception e) {
	    logger.error(e.getMessage(), e);
	} finally {
	    if(p != null)
		p.destroy();
	}
    }

    /**
     * Find an executable on the PATH environment variable and return the full path to it.
     * @param name The name of the executable to find
     * @return An optional containing the full path to the executable if it was found, or an empty optional if it was not found
     */
    public static Optional<String> findExecutableOnPath(String name) {
	for(var dirname : System.getenv().get("PATH").split(File.pathSeparator)) {
	    var file = new File(dirname, name);
	    if(file.isFile() && file.canExecute())
		return Optional.of(file.getAbsolutePath());
	}
	return Optional.empty();
    }
}
