package dk.gtz.graphedit.view.util;

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
}

