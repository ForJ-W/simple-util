-- 如果key1不存在
-- hash set key1 参数1为hashkey value为1
-- 设置 key1过期时间为参数2
-- 返回1
if (redis.call('EXISTS', KEYS[1]) == 0) then
    redis.call('HSET', KEYS[1], ARGV[1], 1);
    redis.call('EXPIRE', KEYS[1], ARGV[2]);
    return 1;
end;
-- 如果key1 hashKey(参数1) 存在
-- 自增 key1 hashKey 的 value 1
-- 设置 key1过期时间为参数2
-- 返回1
if (redis.call('HEXISTS', KEYS[1], ARGV[1]) == 1) then
    redis.call('HINCRBY', KEYS[1], ARGV[1], 1);
    redis.call('EXPIRE', KEYS[1], ARGV[2]);
    return 1;
end;
return 0;