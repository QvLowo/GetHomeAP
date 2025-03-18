package com.qvl.gethomeweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * RedisTemplate 配置key二進制格式序列化為String
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        String型別的key序列化工具
        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        Hash型別的key序列化工具
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redis 連結lettuce工廠
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
//        多個redis指令包成一個transaction，失敗全部rollback
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
