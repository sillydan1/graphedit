package dk.gtz.graphedit.model;

import java.util.Map;
import java.util.UUID;

/**
 * Container for all syntactic elements in a graph.
 * @param declarations A string that can contain extraneous textual syntax such as variable declarations, readme data, value ranges, functions etc.
 * @param vertices A mapping of vertex ids to model vertex-values
 * @param edges A mapping of edge ids to model edge-values
 */
public record ModelGraph(
                String declarations,
                Map<UUID,ModelVertex> vertices,
                Map<UUID,ModelEdge> edges) {}

