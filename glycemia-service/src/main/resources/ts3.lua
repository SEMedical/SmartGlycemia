--get min,max
local user_key=KEYS[1]
local tim=ARGV[1]
local endtim=ARGV[1]
if #ARGV == 2 then
    endtim=ARGV[2]
end
local existing=redis.call('EXISTS',user_key)
if existing~=0 then
    local val=redis.call('TS.RANGE',user_key,tim,endtim)
    return val
end
local list={}
return list