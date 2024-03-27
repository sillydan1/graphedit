---@meta

-- A logger object that integrates with the GraphEdit logging system.
-- Use this object to log messages to the GraphEdit log and alert the user of any issues, warnings etc.
---@class logger
logger = {}

-- Log a debug message.
--  - Debug messages will not be shown in the GraphEdit log, but will be shown in the console.
---@param message string The message to log
function logger:debug(message) end

-- Log a message at the info level.
---@param message string The message to log
function logger:info(message) end

-- Log a message at the warning level.
---@param message string The message to log
function logger:warn(message) end

-- Log a message at the error level.
---@param message string The message to log
function logger:error(message) end


logger_module = {
    -- Log a debug message.
    --  - Debug messages will not be shown in the GraphEdit log, but will be shown in the console.
    ---@param message string The message to log
    debug = function (message) end,

    -- Log a message at the info level.
    ---@param message string The message to log
    info = function (message) end,

    -- Log a message at the warning level.
    ---@param message string The message to log
    warn = function (message) end,

    -- Log a message at the error level.
    ---@param message string The message to log
    error = function (message) end,
}
