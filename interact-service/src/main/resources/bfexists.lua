local candidate=KEYS[1]
local contact=ARGV[1]
return redis.call('CF.EXISTS',contact,candidate)