---@meta

-- Global editor settings object
---@class Cfg
cfg = {}

-- gridSizeX Width of snapgrid cells
---@return NumberProperty
function cfg:gridSizeX() end

-- gridSizeY Height of snapgrid cells
---@return NumberProperty
function cfg:gridSizeY() end

-- gridSnap When true, vertices will snap to the grid
---@return BooleanProperty
function cfg:gridSnap() end

-- useLightTheme When true, the editor will use a light color scheme
---@return BooleanProperty
function cfg:useLightTheme() end

-- autoOpenLastProject When true, the last opened project will be automatically opened next time you start the editor
---@return BooleanProperty
function cfg:autoOpenLastProject() end

-- showInspectorPane (Deprecated) doesn't do anything anymore
---@deprecated
---@return BooleanProperty
function cfg:showInspectorPane() end

-- showInfoToasts When true, will display toasts on logger.info calls
---@return BooleanProperty
function cfg:showInfoToasts() end

-- showWarnToasts When true, will display toasts on logger.warn calls
---@return BooleanProperty
function cfg:showWarnToasts() end

-- showErrorToasts When true, will display toasts on logger.error calls
---@return BooleanProperty
function cfg:showErrorToasts() end

-- showTraceToasts When true, will display toasts on logger.trace calls
---@return BooleanProperty
function cfg:showTraceToasts() end

-- lastOpenedProject Filepath to the last opened graphedit project file
---@return StringProperty
function cfg:lastOpenedProject() end

-- recentProjects List of filepaths that have been recently opened
---@return ListProperty
function cfg:recentProjects() end

-- disabledPlugins List of plugin names that are loaded, but not initialized
---@return ListProperty
function cfg:disabledPlugins() end

-- tipIndex Index of the last shown tip
---@return NumberProperty
function cfg:tipIndex() end

-- showTips When true, will show tips on startup
---@return BooleanProperty
function cfg:showTips() end
