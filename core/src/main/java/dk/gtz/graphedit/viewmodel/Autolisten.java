package dk.gtz.graphedit.viewmodel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for fields within a {@link AutoProperty} class that should be automatically listened to.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Autolisten {}
