package com.shankhadeepghoshal.learnkafka.taxiprocessor.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
  @Bean
  public RedisClient redisClient(@Value("${redis.uri}") final String redisUriString) {
    return RedisClient.create(redisUriString);
  }

  @Bean
  public GenericObjectPool<StatefulRedisConnection<String, String>> objectPool(
      final RedisClient client) {
    return ConnectionPoolSupport.createGenericObjectPool(
        client::connect, new GenericObjectPoolConfig<>());
  }
}
