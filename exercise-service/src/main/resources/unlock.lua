-- The key of the lock
local key = KEYS[1]
local threadId = ARGV[1]
local id = redis.call('get',key)
if(id==threadId) then
        return redis.call('del',key)
end
return 0