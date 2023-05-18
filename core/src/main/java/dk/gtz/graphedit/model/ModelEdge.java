package dk.gtz.graphedit.model;

import java.util.UUID;

/**
 * An edge from some source to some target. May connect any syntactic element
 */
public record ModelEdge(UUID source, UUID target) {}

