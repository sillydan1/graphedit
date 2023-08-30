package dk.gtz.graphedit.view.util;

import java.io.File;

public class PlatformUtils {
    public static boolean isSystemMenuBarSupported() {
	if(isWindows())
	    return true;
	if(isMac())
	    return true;
	if(isUnix())
	    return isGtk();
	return false;
    }

    public static boolean isWindows() {
	return atlantafx.base.util.PlatformUtils.isWindows();
    }

    public static boolean isMac() {
	return atlantafx.base.util.PlatformUtils.isMac();
    }

    public static boolean isUnix() {
	return atlantafx.base.util.PlatformUtils.isUnix();
    }

    public static boolean isGtk() {
	var platform = System.getProperty("javafx.platform");
	return platform != null && platform.equals("gtk");
    }

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
}

