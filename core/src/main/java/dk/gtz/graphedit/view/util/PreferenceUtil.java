package dk.gtz.graphedit.view.util;

import java.util.prefs.Preferences;

public class PreferenceUtil {
    public static String USE_LIGHT_THEME = "graphedit.use_light_theme";
    
    public static boolean getUseLightTheme() {
	return Preferences.userRoot().getBoolean(USE_LIGHT_THEME, false);
    }

    public static void setUseLightTheme(boolean value) {
	Preferences.userRoot().putBoolean(USE_LIGHT_THEME, value);
    }
}

