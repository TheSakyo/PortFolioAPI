package fr.thesakyo.portfolioapi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisSecurityConfig {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    /**************************************************/

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        RedisSerializer<Object> jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setKeySerializer(jsonSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        return template;
    }
}
