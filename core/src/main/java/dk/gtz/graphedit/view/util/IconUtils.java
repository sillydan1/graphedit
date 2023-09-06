package dk.gtz.graphedit.view.util;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * General utilities relating to {@code FontIcon} creation and manipulation
 */
public class IconUtils {
    public static FontIcon getFileTypeIcon(String fileType) {
	if(fileType == null)
	    return new FontIcon(BootstrapIcons.QUESTION_DIAMOND);
	switch(fileType) {
	    // add more as needed
	    case "application/json":
	    case "application/xml":
		return new FontIcon(BootstrapIcons.FILE_EARMARK_CODE);
	    default:
		return new FontIcon(BootstrapIcons.FILE_EARMARK);
	}
    }
}

