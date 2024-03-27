Module = {
    conf = {
        hest = function ()
            logger:info('hest')
        end
    },
}

Module.conf.hest()

function Hello()
    logger:info("Hello from Lua file!")
    return 'Hello from Lua file!'
end

logger:info('hello from lua file globally')

Int = java.import('int')
java.array(Int, 1)

logger_module.info('hello from lua file globally')
