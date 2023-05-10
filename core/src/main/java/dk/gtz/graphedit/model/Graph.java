package dk.gtz.graphedit.model;

import java.util.Map;
import java.util.UUID;

/**
 * Container for all syntactic elements in a graph.
 */
public record Graph(
                String declarations,
                Map<UUID,Vertex> vertices,
                Map<UUID,Edge> edges) {}

