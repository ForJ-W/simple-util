简单工具...
不懂找我

BeanHelper见test

ExcelHelper见test

YouDaoTranslate见test


@Log:
    
    1.打印方法运行时间
    2.打印类中包含某个注解的所有属性(toString())
    

@CacheListener & @Cache:
    
    1.必须组合使用
    2.@CacheListener监听类中所有方法,当发生增删改时刷新指定rediskey缓存状态,默认rediskey为类中所有查询方法
    3.@Cache缓存当前方法运行结果,可自定义reids key前缀,默认以及具体规则见CacheAdvice中keyHandler方法
    
