package dk.gtz.graphedit.model;

import java.util.Map;

/**
 * Full file-on-disk model. Will include everything a graphedit project file needs
 */
public record ModelProjectResource(Map<String,String> metadata, ModelGraph syntax) {}

