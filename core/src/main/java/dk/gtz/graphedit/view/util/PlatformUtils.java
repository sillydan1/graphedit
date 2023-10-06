package dk.gtz.graphedit.view.util;

import java.io.File;

/**
 * General utilities relating to the operating system
 */
public class PlatformUtils {
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
     * @param executableName
     * @return
     */
    public static boolean isProgramInstalled(String executableName) {
	var pathVariable = System.getenv("PATH");
	var pathDirectories = pathVariable.split(File.pathSeparator);
	for (var directory : pathDirectories) {
	    var executableFile = new File(directory, executableName);
	    if (executableFile.exists() && !executableFile.isDirectory())
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
}

