package dk.gtz.graphedit.model;

import java.util.Map;
import java.util.UUID;

public record Syntax(
                String declarations,
                Map<UUID,Vertex> vertices,
                Map<UUID,Edge> edges) {}

