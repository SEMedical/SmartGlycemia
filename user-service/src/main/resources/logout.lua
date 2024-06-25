local session_set=KEYS[1]
local token=KEYS[2]
redis.call('ZREM', session_set, token)
redis.call('DEL',token)