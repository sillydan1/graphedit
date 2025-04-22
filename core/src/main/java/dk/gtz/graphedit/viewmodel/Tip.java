package dk.gtz.graphedit.viewmodel;

import java.util.Optional;

import dk.gtz.graphedit.view.TipOfTheDayController;
import javafx.scene.control.Tooltip;

/**
 * Represents a tip that can be shown to the user.
 * Note, not to be confused with a {@link Tooltip}, this represents a tip object
 * for the {@link TipOfTheDayController} view.
 * 
 * @param category    The category of the tip
 * @param description The description / message of the tip
 * @param imageWidth  The width of the image to be shown with the tip, ignored
 *                    if no image is provided.
 * @param image       The image to be shown with the tip, optional.
 */
public record Tip(String category, String description, Integer imageWidth, Optional<String> image) {
	/**
	 * Creates a new tip with the given category and description.
	 * 
	 * @param category    The category of the tip
	 * @param description The description / message of the tip
	 */
	public Tip(String category, String description) {
		this(category, description, 500, Optional.empty());
	}

	/**
	 * Creates a new tip with the given category, description and image.
	 * 
	 * @param category    The category of the tip
	 * @param description The description / message of the tip
	 * @param image       The image to be shown with the tip
	 */
	public Tip(String category, String description, String image) {
		this(category, description, 500, Optional.of(image));
	}

	/**
	 * Creates a new tip with the given category, description and image.
	 * 
	 * @param category    The category of the tip
	 * @param description The description / message of the tip
	 * @param imageWidth  The width of the image to be shown with the tip
	 * @param image       The image to be shown with the tip
	 */
	public Tip(String category, String description, Integer imageWidth, String image) {
		this(category, description, imageWidth, Optional.of(image));
	}
}
