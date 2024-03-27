---@meta

-- A logger object that integrates with the GraphEdit logging system.
-- Use this object to log messages to the GraphEdit log and alert the user of any issues, warnings etc.
---@class Log
log = {}

-- Log a debug message.
--  - Debug messages will not be shown in the GraphEdit log, but will be shown in the console.
---@param message string The message to log
function log:debug(message) end

-- Log a message at the info level.
---@param message string The message to log
function log:info(message) end

-- Log a message at the warning level.
---@param message string The message to log
function log:warn(message) end

-- Log a message at the error level.
---@param message string The message to log
function log:error(message) end
