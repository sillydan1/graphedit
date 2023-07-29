package dk.gtz.graphedit.model;

import java.util.List;
import java.util.Map;

public record ModelRunTarget(
    String name,
    String command,
    List<String> arguments,
    Map<String, String> environment) {}

