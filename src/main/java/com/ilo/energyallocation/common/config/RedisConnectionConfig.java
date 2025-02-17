package com.ilo.energyallocation.common.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConnectionConfig {

    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, byte[]> connection;

    public RedisConnectionConfig(RedisProperties redisProperties) {
        this.redisClient = RedisClient.create(redisProperties.getUrl());
        RedisCodec<String, byte[]> codec = RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE);
        this.connection = redisClient.connect(codec);
    }

    @Bean
    public StatefulRedisConnection<String, byte[]> redisConnection() {
        return connection;
    }

    @PreDestroy
    public void cleanup() {
        if (connection != null) {
            connection.close();
        }
        if (redisClient != null) {
            redisClient.shutdown();
        }
    }
}