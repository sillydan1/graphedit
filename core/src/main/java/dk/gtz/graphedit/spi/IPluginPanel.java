package dk.gtz.graphedit.spi;

import org.kordamp.ikonli.javafx.FontIcon;

import javafx.scene.Node;

/**
 * Interface for third-party plugin side-panels.
 */
public interface IPluginPanel {
	/**
	 * Get the on-mouse-hover tooltip displayed when the user hovers over the plugin
	 * icon
	 * 
	 * @return A (possibly multiline) string to display as a tooltip
	 */
	String getTooltip();

	/**
	 * Get the icon representing the plugin. Typically a {@link FontIcon}
	 * 
	 * @return A node with an icon
	 */
	Node getIcon();

	/**
	 * Get the javafx panel to show when the plugin is selected
	 * 
	 * @return A javafx node
	 */
	Node getPanel();
}
