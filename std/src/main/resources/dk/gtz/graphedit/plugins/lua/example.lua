-- Get stuff from the Dependency Injection container
-- TODO: manually importing like this may become a bit cumbersome, maybe we should add a helper function for this
local IBufferContainer = java.import("dk.gtz.graphedit.ViewModel.IBufferContainer")
ge.api.dep:get(IBufferContainer):get("hest")

-- Add plugins
ge.api.add_plugin({
    name = "Example",
    description = "Example plugin",
    on_start = function() end,
    on_destroy = function() end,
    -- TODO:
    -- syntax_factories = {},
    -- panels = {},
    -- importer = function(paths) end,
    -- exporter = function(resource, new_path) end,
})
-- ge.api.get_plugin("Example").panels
