local user_key=KEYS[1]
local startp=ARGV[1]
local endp=ARGV[2]
local existed=redis.call('EXISTS',user_key)
if existed==0 then
    redis.call('TS.CREATE',user_key,'RETENTION',31536000000)
end
local ts=redis.call('TS.RANGE',user_key,startp,endp)
return ts