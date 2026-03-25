package com.esther.fluxsync.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
class RedisConfig {

//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        return new JedisConnectionFactory();
//    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate( RedisConnectionFactory redisConnectionFactory ) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory( redisConnectionFactory );

        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        template.setKeySerializer( stringSerializer );
        template.setHashKeySerializer( stringSerializer );
        template.setValueSerializer( jsonSerializer );
        template.setHashValueSerializer( jsonSerializer );

        template.afterPropertiesSet();
        return template;

    }

}
