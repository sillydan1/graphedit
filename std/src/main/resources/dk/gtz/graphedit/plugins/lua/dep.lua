---@meta

---@class DI
di = {}

---@param key string | jclass
---@param obj any
function di:add(key, obj) end

---@param key string | jclass
---@param supplier fun():any
function di:add(key, supplier) end

function di:lock() end

---@param key string | jclass
---@return any
function di:get(key) end

---@param key string | jclass
---@return boolean
function di:contains(key) end

---@return table
function di:getAllKeys() end
