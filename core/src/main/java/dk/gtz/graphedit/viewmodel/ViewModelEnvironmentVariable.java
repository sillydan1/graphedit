package dk.gtz.graphedit.viewmodel;

import javafx.beans.property.StringProperty;

/**
 * Viewmodel representation of an environment variable and the assigned value of
 * it.
 * 
 * @param key   The environment variable name
 * @param value The value for the environment variable
 */
public record ViewModelEnvironmentVariable(StringProperty key, StringProperty value) {
}
