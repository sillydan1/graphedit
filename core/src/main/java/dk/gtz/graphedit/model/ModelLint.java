package dk.gtz.graphedit.model;

import java.util.List;
import java.util.UUID;

public record ModelLint(
        String lintIdentifier,
        ModelLintSeverity severity,
        String title,
        String message,
        List<UUID> affectedElements,
        List<List<ModelPoint>> affectedRegions) {}

