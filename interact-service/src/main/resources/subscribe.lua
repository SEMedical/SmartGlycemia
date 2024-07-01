-- 1. 向列表中左推入一个值
redis.call('LPUSH', KEYS[1], KEYS[2])
local id=ARGV[1]
local name=ARGV[2]
local age=ARGV[3]
local expire_time=ARGV[4]-- unit:second
if name == nil then
    name = "no name"
end
if age == nil then
    age = "Unknown"
end
redis.call('HSET', KEYS[2], "id", id)
redis.call('HSET', KEYS[2], "name", name)
redis.call('HSET', KEYS[2], "age", age)
redis.call('EXPIRE', KEYS[2], expire_time)

local list_length = redis.call('LLEN', KEYS[1])
for i=0,list_length-1 do
    local element = redis.call('LINDEX', KEYS[1], i)
    if element == KEYS[2] then
        goto continue
    end
    local key_exists = redis.call('EXISTS', element)
    if key_exists == 0 then
        redis.call('LREM', KEYS[1], 0, element)
    end
    ::continue::
end