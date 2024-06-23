local session_set=KEYS[1]
local token=KEYS[2]
local icon=ARGV[1]
local user_id=ARGV[2]
local role=ARGV[3]
local name=ARGV[4]
local now=tonumber(ARGV[5])
local begin_t=now - 30 * 60* 1000
local size = redis.call('ZCARD', session_set)
redis.call('ZREMRANGEBYSCORE', session_set, -math.huge,begin_t)
local size2 = redis.call('ZCARD', session_set)
redis.log(redis.LOG_NOTICE, size-size2 .. " sessions are removed")
local count = redis.call('ZCOUNT', session_set, begin_t, now)
if count < 10 then
    redis.call('ZADD', session_set, now,token)
    redis.call('HSET',token,'icon',icon)
    redis.call('HSET',token,'name',name)
    redis.call('HSET',token,'userId',user_id)
    redis.call('HSET',token,'role',role)
    redis.call('EXPIRE', token, 30 * 60)
    return true
else
    return false
end