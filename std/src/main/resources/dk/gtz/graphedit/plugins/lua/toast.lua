---@meta

---@class Toast
Toast = {}

-- Show an "info" styled toast.
---@param message string
function Toast:info(message) end

-- Show an "success" styled toast.
---@param message string
function Toast:success(message) end

-- Show an "warn" styled toast.
---@param message string
function Toast:warn(message) end

-- Show an "error" styled toast.
---@param message string
function Toast:error(message) end
