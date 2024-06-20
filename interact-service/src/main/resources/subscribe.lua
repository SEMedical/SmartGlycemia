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