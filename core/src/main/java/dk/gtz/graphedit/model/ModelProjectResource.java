package dk.gtz.graphedit.model;

import java.util.Map;

/**
 * Full file-on-disk model. Will include everything a graphedit project file needs
 * 
 * Not to be confused with {@link ModelProject}, this class represents a graph and it's metadata.
 */
public record ModelProjectResource(Map<String,String> metadata, ModelGraph syntax) {}

