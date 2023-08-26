package dk.gtz.graphedit.view.util;

public class PlatformUtils {
    public static boolean isSystemMenuBarSupported() {
	var platform = System.getProperty("javafx.platform");
	if(atlantafx.base.util.PlatformUtils.isWindows())
	    return true;
	if(atlantafx.base.util.PlatformUtils.isMac())
	    return true;
	if(atlantafx.base.util.PlatformUtils.isUnix())
	    if(platform != null && platform.equals("gtk"))
		return true;
	return false;
    }
}

