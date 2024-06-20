local follower_key = KEYS[1] .. ARGV[1]
local subscribe_doctor_key = KEYS[2] .. ARGV[2]
redis.call('DEL', follower_key)
redis.call('LREM', subscribe_doctor_key, 0, follower_key)