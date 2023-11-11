package dk.gtz.graphedit.viewmodel;

import java.util.UUID;

/**
 * Viewmodel representation of a selection
 * @param id The id of the syntactic element that has been selected
 * @param selectable The selectable instance that has been selected
 */
public record ViewModelSelection(UUID id, ISelectable selectable) {}

