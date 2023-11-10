package dk.gtz.graphedit.viewmodel;

import javafx.beans.Observable;

/**
 * Representation of an {@link Observable} with a {@link String} name
 * @param name The name of the inspectable property
 * @param property The property of the inspectable property
 */
public record InspectableProperty(String name, Observable property) {}

