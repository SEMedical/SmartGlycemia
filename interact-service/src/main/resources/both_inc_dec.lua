local followee_key = KEYS[1]
local follower_key = KEYS[2]
local command=ARGV[1]
redis.call(command, followee_key)
redis.call(command,follower_key)