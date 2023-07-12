package dk.gtz.graphedit.view.util;

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PreferenceUtil {
    private static Logger logger = LoggerFactory.getLogger(PreferenceUtil.class);
    private static Preferences applicationPreferences = Preferences.userNodeForPackage(PreferenceUtil.class);
    private static String USE_LIGHT_THEME = "graphedit.use_light_theme";
    private static String LAST_OPENED_PROJECT = "graphedit.last_opened_project";

    public static void clearAllPreferences() {
	try {
	    applicationPreferences.clear();
	} catch (BackingStoreException e) {
	    logger.error(e.getMessage());
	}
    }

    public static boolean lightTheme() {
	var defaultValue = false;
	return applicationPreferences.getBoolean(USE_LIGHT_THEME, defaultValue);
    }

    public static boolean lightTheme(boolean value) {
	Preferences.userRoot().putBoolean(USE_LIGHT_THEME, value);
	return lightTheme();
    }

    public static String lastOpenedProject() {
	var defaultValue = System.getProperty("user.dir") + File.separator + "project.json";
	return applicationPreferences.get(LAST_OPENED_PROJECT, defaultValue);
    }

    public static String lastOpenedProject(String value) {
	applicationPreferences.put(LAST_OPENED_PROJECT, value);
	return lastOpenedProject();
    }
}

