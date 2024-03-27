---@meta
-- TODO: bind function

---@class NumberProperty
NumberProperty = {}

-- Gets the value of the property.
---@return number
function NumberProperty:get() end

-- Sets the value of the property.
---@param value number
function NumberProperty:set(value) end


---@class StringProperty
StringProperty = {}

-- Gets the value of the property.
---@return string
function StringProperty:get() end

-- Sets the value of the property.
---@param value string
function StringProperty:set(value) end


---@class BooleanProperty
BooleanProperty = {}

-- Gets the value of the property.
---@return boolean
function BooleanProperty:get() end

-- Sets the value of the property.
---@param value boolean
function BooleanProperty:set(value) end


---@class ListProperty
ListProperty = {}

-- Gets the value of the property.
---@return table
function ListProperty:get() end

-- Sets the value of the property.
---@param value table
function ListProperty:set(value) end
