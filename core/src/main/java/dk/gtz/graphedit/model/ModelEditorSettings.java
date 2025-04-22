package dk.gtz.graphedit.model;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import dk.gtz.graphedit.util.EditorActions;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;

/**
 * General editor settings model object containing a users' prefered theme,
 * recent projects and other edior-wide preferences and settings.
 * This is meant to be serialized and deserialized to/from disk
 * 
 * @param gridSizeX           Width of snapgrid cells
 * @param gridSizeY           Height of snapgrid cells
 * @param gridSnap            When true, vertices will snap to the grid
 * @param useLightTheme       When true, the editor will use a light color
 *                            scheme
 * @param autoOpenLastProject When true, the last opened project will be
 *                            automatically opened next time you start the
 *                            editor
 * @param showInspectorPane   (Deprecated) doesn't do anything anymore
 * @param showInfoToasts      When true, will display toasts on logger.info
 *                            calls
 * @param showWarnToasts      When true, will display toasts on logger.warn
 *                            calls
 * @param showErrorToasts     When true, will display toasts on logger.error
 *                            calls
 * @param showTraceToasts     When true, will display toasts on logger.trace
 *                            calls
 * @param lastOpenedProject   Filepath to the last opened graphedit project file
 * @param recentProjects      List of filepaths that have been recently opened
 * @param disabledPlugins     List of plugin filepaths that are disabled
 * @param tipIndex            Index of the current tip being shown
 * @param showTips            When true, will show a tip of the day on startup
 */
public record ModelEditorSettings(
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
		List<String> disabledPlugins,
		int tipIndex,
		boolean showTips) {

	/**
	 * Constructs a ModelEditorSettings instance with default values.
	 */
	public ModelEditorSettings() {
		this(20.0d, 20.0d, true, false, true, false, true, true, true, false, "", new ArrayList<>(),
				new ArrayList<>(), 0, true);
	}

	/**
	 * Constructs a ModelEditorSettings instance based on the associated ViewModel
	 * 
	 * @param viewmodel The viewmodel to base the new instance value off of
	 */
	public ModelEditorSettings(ViewModelEditorSettings viewmodel) {
		this(viewmodel.gridSizeX().get(),
				viewmodel.gridSizeY().get(),
				viewmodel.gridSnap().get(),
				viewmodel.useLightTheme().get(),
				viewmodel.autoOpenLastProject().get(),
				viewmodel.showInspectorPane().get(),
				viewmodel.showInfoToasts().get(),
				viewmodel.showWarnToasts().get(),
				viewmodel.showErrorToasts().get(),
				viewmodel.showTraceToasts().get(),
				viewmodel.lastOpenedProject().get(),
				new ArrayList<String>(viewmodel.recentProjects().get()),
				new ArrayList<String>(viewmodel.disabledPlugins().get()),
				viewmodel.tipIndex().get(),
				viewmodel.showTips().get());
	}

	/**
	 * Get the file of the editor settings.
	 * Note that the filepath may be different depending on the operating system and
	 * $HOME variable
	 * 
	 * @return The OS-specific file path to editor settings
	 */
	public static Path getEditorSettingsFile() {
		return Path.of(EditorActions.getConfigDir() + File.separator + "graphedit-settings.json");
	}
}
