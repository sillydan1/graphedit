package dk.gtz.graphedit.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record ModelLint(
        String modelKey,
        String lintIdentifier,
        ModelLintSeverity severity,
        String title,
        String message,
        Optional<String> lintDescription,
        List<UUID> affectedElements,
        List<List<ModelPoint>> affectedRegions) {}

