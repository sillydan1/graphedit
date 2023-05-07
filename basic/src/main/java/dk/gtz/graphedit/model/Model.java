package dk.gtz.graphedit.model;

import java.util.Map;

public record Model(Map<String,String> metadata, Syntax syntax) {}

