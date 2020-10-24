-- hashkey 不存在
if (redis.call('HEXISTS', KEYS[1], ARGV[1]) == 0) then
    return nil;
end;
-- 如果hash自减1大于0 续期
local count = redis.call('HINCRBY', KEYS[1], ARGV[1], -1);
if (count > 0) then
    redis.call('EXPIRE', KEYS[1], ARGV[2]);
    return nil;
else
--  否则删除key
    redis.call('DEL', KEYS[1]);
    return nil;
end;