package com.simple.util.spring.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author wujing
 * @version 1.0.0
 * @date 2020/10/24 16:05
 */
@Configuration
public class AutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    public <K, V> RedisTemplate<K, V> redisTemplate(RedisConnectionFactory factory) {

        RedisTemplate<K, V> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        GenericToStringSerializer<Object> genericToStringSerializer = new GenericToStringSerializer<>(Object.class);
        template.setValueSerializer(genericToStringSerializer);
        template.setKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}
