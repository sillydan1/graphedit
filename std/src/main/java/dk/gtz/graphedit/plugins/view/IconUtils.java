package dk.gtz.graphedit.plugins.view;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * General utilities relating to {@code FontIcon} creation and manipulation
 */
public class IconUtils {
    public static FontIcon getFileTypeIcon(String fileMimeType) {
	if(fileMimeType == null)
	    return new FontIcon(BootstrapIcons.QUESTION_DIAMOND);
	switch(fileMimeType) {
	    // add more as needed
	    case "text/plain":
		return new FontIcon(BootstrapIcons.FILE_TEXT);
	    case "application/json":
	    case "application/xml":
		return new FontIcon(BootstrapIcons.FILE_EARMARK_CODE);
	    default:
		return new FontIcon(BootstrapIcons.FILE_EARMARK);
	}
    }
}

