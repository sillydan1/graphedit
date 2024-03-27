---@meta

-- The `action` module provides a way to interact with the application.
-- Many of the available functions take Java objects as arguments, so please refer to the Java documentation for reference.
-- Java documentation can be found at: https://javadoc.graphedit.gtz.dk/dk/gtz/graphedit/util/EditorActions.html
---@class Action
action = {}

function action:quit() end
function action:restart() end
---@param listener fun()
function action:addSaveListener(listener) end
---@param listener fun()
function action:removeSaveListener(listener) end
function action:saveAs() end
function action:save() end
function action:loadEditorSettings() end
---@param settings Cfg
function action:saveEditorSettings(settings) end
---@param file string
function action:openProject(file) end
function action:saveProject(project, projectFilePath) end
function action:saveProject() end
function action:openProjectPicker(window) end
function action:newFile() end
function action:toggleTheme() end
function action:openEditorSettings() end
function action:openTipOfTheDay() end
function action:openProjectSettings() end
function action:openRunTargetsEditor() end
function action:openSearchPane() end
function action:openAboutPane() end
function action:openModal(node, title) end
---@param fxmlFile string
---@param title string
function action:openModal(fxmlFile, title) end
function action:undo() end
function action:redo() end
function action:executeRunTarget(runTarget) end
function action:showConfirmDialog(title, question, window) end
function action:showConfirmAllDialog(title, question, window) end
function action:createNewModelFile(model) end
function action:openJsonFile() end
function action:openJsonFiles() end
function action:openFile(description, filterTypes) end
function action:openFile(filter) end
function action:openFiles(description, filterTypes) end
function action:openFiles(filter) end
function action:openFolder() end
function action:exportFiles(exporter, files) end
function action:exportFiles(exporter, files, exportDirectory) end
function action:openModel() end
function action:openModel(path) end
function action:createNewModelFile() end
function action:saveModelToFile(newFilePath, newModel) end
function action:createNewModel(name) end

-- TODO: This should be in the info module instead / as well
function action:getConfigDir() end
