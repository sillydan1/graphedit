---@meta

---@class BufferContainer
buf = {}

---@param key string
---@return jobject # A ViewModelProjectResource java object
function buf:get(key) end

---@param key string
function buf:close(key) end

---@param key string
function buf:open(key) end

---@param key string
---@param model jobject # A ViewModelProjectResource java object
function buf:open(key, model) end

---@param key string
---@return boolean
function buf:contains(key) end

---@return table
function buf:getBuffers() end

---@return jobject # A ViewModelProjectResource java object
function buf:getCurrentlyFocusedBuffer() end
