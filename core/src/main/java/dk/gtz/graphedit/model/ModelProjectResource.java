package dk.gtz.graphedit.model;

import java.util.Map;

/**
 * Full file-on-disk model. Will include everything a graphedit project file needs
 * 
 * Not to be confused with {@link ModelProject}, this class represents a graph and it's metadata.
 * @param metadata A map of generic string-encoded metadata, useful for syntax=specific data
 * @param syntax The graph syntax itself
 */
public record ModelProjectResource(Map<String,String> metadata, ModelGraph syntax) {}

