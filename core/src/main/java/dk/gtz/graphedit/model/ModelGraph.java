package dk.gtz.graphedit.model;

import java.util.Map;
import java.util.UUID;

/**
 * Container for all syntactic elements in a graph.
 */
public record ModelGraph(
                String declarations,
                Map<UUID,ModelVertex> vertices,
                Map<UUID,ModelEdge> edges) {}

