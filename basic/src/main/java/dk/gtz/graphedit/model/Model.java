package dk.gtz.graphedit.model;

import java.util.Map;

/**
 * Full file-on-disk model. Will include everything a graphedit project file needs
 */
public record Model(Map<String,String> metadata, Graph syntax) {}

