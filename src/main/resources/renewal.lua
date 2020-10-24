
if (redis.call('EXISTS', KEYS[1]) == 1) then
    redis.call('EXPIRE', KEYS[1], ARGV[1]);
    return nil;
end;