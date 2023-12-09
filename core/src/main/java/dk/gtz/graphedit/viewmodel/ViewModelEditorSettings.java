package dk.gtz.graphedit.viewmodel;

import java.util.List;

import dk.gtz.graphedit.model.ModelEditorSettings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

/**
 * View model representation of the global editor settings
 * @param gridSizeX Width of snapgrid cells
 * @param gridSizeY Height of snapgrid cells
 * @param gridSnap When true, vertices will snap to the grid
 * @param useLightTheme When true, the editor will use a light color scheme
 * @param autoOpenLastProject When true, the last opened project will be automatically opened next time you start the editor
 * @param showInspectorPane (Deprecated) doesn't do anything anymore
 * @param showInfoToasts When true, will display toasts on logger.info calls
 * @param showWarnToasts When true, will display toasts on logger.warn calls
 * @param showErrorToasts When true, will display toasts on logger.error calls
 * @param showTraceToasts When true, will display toasts on logger.trace calls
 * @param lastOpenedProject Filepath to the last opened graphedit project file
 * @param recentProjects List of filepaths that have been recently opened
 */
public record ViewModelEditorSettings(
                DoubleProperty gridSizeX,
                DoubleProperty gridSizeY,
                BooleanProperty gridSnap,
                BooleanProperty useLightTheme,
                BooleanProperty autoOpenLastProject,
                @Deprecated BooleanProperty showInspectorPane,
                BooleanProperty showInfoToasts,
                BooleanProperty showWarnToasts,
                BooleanProperty showErrorToasts,
                BooleanProperty showTraceToasts,
                StringProperty lastOpenedProject,
                ListProperty<String> recentProjects,
                ListProperty<String> disabledPlugins) {

        /**
         * Construct a new instance
         * @param settings The settings model object to base on
         */
        public ViewModelEditorSettings(ModelEditorSettings settings) {
                this(
                        settings.gridSizeX(),
                        settings.gridSizeY(),
                        settings.gridSnap(),
                        settings.useLightTheme(),
                        settings.autoOpenLastProject(),
                        settings.showInspectorPane(),
                        settings.showInfoToasts(),
                        settings.showWarnToasts(),
                        settings.showErrorToasts(),
                        settings.showTraceToasts(),
                        settings.lastOpenedProject(),
                        new SimpleListProperty<String>(FXCollections.observableArrayList()),
                        new SimpleListProperty<String>(FXCollections.observableArrayList())
                    );
                this.recentProjects.addAll(settings.recentProjects());
                this.disabledPlugins.addAll(settings.disabledPlugins());
        }

        /**
         * Construct a new instance
         * @param gridSizeX Width of snapgrid cells
         * @param gridSizeY Height of snapgrid cells
         * @param gridSnap When true, vertices will snap to the grid
         * @param useLightTheme When true, the editor will use a light color scheme
         * @param autoOpenLastProject When true, the last opened project will be automatically opened next time you start the editor
         * @param showInspectorPane (Deprecated) doesn't do anything anymore
         * @param showInfoToasts When true, will display toasts on logger.info calls
         * @param showWarnToasts When true, will display toasts on logger.warn calls
         * @param showErrorToasts When true, will display toasts on logger.error calls
         * @param showTraceToasts When true, will display toasts on logger.trace calls
         * @param lastOpenedProject Filepath to the last opened graphedit project file
         * @param recentProjects List of filepaths that have been recently opened
         */
        public ViewModelEditorSettings(
                        double gridSizeX, 
                        double gridSizeY,
                        boolean gridSnap,
                        boolean useLightTheme,
                        boolean autoOpenLastProject,
                        @Deprecated boolean showInspectorPane,
                        boolean showInfoToasts,
                        boolean showWarnToasts,
                        boolean showErrorToasts,
                        boolean showTraceToasts,
                        String lastOpenedProject,
                        List<String> recentProjects,
                        List<String> disabledPlugins) {
                this(
                        new SimpleDoubleProperty(gridSizeX),
                        new SimpleDoubleProperty(gridSizeY),
                        new SimpleBooleanProperty(gridSnap),
                        new SimpleBooleanProperty(useLightTheme),
                        new SimpleBooleanProperty(autoOpenLastProject),
                        new SimpleBooleanProperty(showInspectorPane),
                        new SimpleBooleanProperty(showInfoToasts),
                        new SimpleBooleanProperty(showWarnToasts),
                        new SimpleBooleanProperty(showErrorToasts),
                        new SimpleBooleanProperty(showTraceToasts),
                        new SimpleStringProperty(lastOpenedProject),
                        new SimpleListProperty<String>(FXCollections.observableArrayList()),
                        new SimpleListProperty<String>(FXCollections.observableArrayList())
                    );
                this.recentProjects.addAll(recentProjects);
                this.disabledPlugins.addAll(disabledPlugins);
        }
}
