package dk.gtz.graphedit.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dk.gtz.graphedit.viewmodel.IBufferContainer;

/**
 * Model object of a lint.
 * A Lint is a special kind of diagnostic annotation that can provide smart
 * meta-insights about sections of a graph.
 * 
 * @param modelKey         The key of the related model (see
 *                         {@link IBufferContainer})
 * @param lintIdentifier   Unique identifier for the type of lint
 * @param severity         The severity level of the lint
 * @param title            A very brief headline describing the lint
 * @param message          A detailed message displayed to the user
 * @param lintDescription  A general description of what this kind of lint
 *                         represents
 * @param affectedElements A list of which syntactic elements should be
 *                         highlighted
 * @param affectedRegions  A list of regions in the graph that should be
 *                         highlighted
 */
public record ModelLint(
		String modelKey,
		String lintIdentifier,
		ModelLintSeverity severity,
		String title,
		String message,
		Optional<String> lintDescription,
		List<UUID> affectedElements,
		List<List<ModelPoint>> affectedRegions) {
}
