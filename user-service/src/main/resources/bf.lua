local contacts=KEYS
local contact=ARGV[1]
if ARGV[2]=='DEL' then
    redis.call('DEL',contact)
end
local existing=redis.call('EXISTS',contact)
if existing==0 then
    redis.call('CF.RESERVE',contact,"10000","BUCKETSIZE", "8", "MAXITERATIONS", "20" ,"EXPANSION", "2")
end
for i=1,#contacts do
    local c=contacts[i]
    redis.call('CF.ADD',contact,c)
end
