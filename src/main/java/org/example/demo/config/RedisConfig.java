package org.example.demo.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
@EnableCaching
@Slf4j
public class RedisConfig implements CachingConfigurer {

  @Override
  @Bean // 2. Override the interface method
  public CacheErrorHandler errorHandler() {
    return new CacheErrorHandler() {
      @Override
      public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.warn(
            "Redis serialization error on cache='{}', key='{}'. Evicting...", cache.getName(), key);
        try {
          cache.evict(key); // Automatically clears the "bad" data
        } catch (Exception e) {
          log.error("Failed to evict stale key: {}", key, e);
        }
      }

      @Override
      public void handleCachePutError(
          RuntimeException exception, Cache cache, Object key, Object value) {
        log.error("Cache put error for cache='{}', key='{}'", cache.getName(), key, exception);
      }

      @Override
      public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.error("Cache evict error for cache='{}', key='{}'", cache.getName(), key, exception);
      }

      @Override
      public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.error("Cache clear error for cache='{}'", cache.getName(), exception);
      }
    };
  }

  @Bean
  public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    // 1. Configure the ObjectMapper for Records and Java 8 Dates
    ObjectMapper mapper =
        JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .addModule(new ParameterNamesModule()) // Required for Records
            .activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.EVERYTHING, // Required for final classes/Records
                JsonTypeInfo.As.PROPERTY)
            .build();

    // 2. Use the new non-deprecated factory method
    RedisSerializer<Object> jsonSerializer = RedisSerializer.json();

    // 3. Setup Default Configuration
    RedisCacheConfiguration config =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    RedisSerializer.string()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
            .disableCachingNullValues();

    // 4. Custom TTLs (Matching your Sword/Yangon Fast Pass requirements)
    Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
    cacheConfigs.put("series:list", config.entryTtl(Duration.ofHours(1)));
    cacheConfigs.put("series:detail", config.entryTtl(Duration.ofMinutes(45)));
    cacheConfigs.put("user:library", config.entryTtl(Duration.ofMinutes(25)));
    cacheConfigs.put("user:progress", config.entryTtl(Duration.ofMinutes(10)));
    cacheConfigs.put("series:search", config.entryTtl(Duration.ofMinutes(15)));
    cacheConfigs.put("series:genre", config.entryTtl(Duration.ofMinutes(30)));

    return RedisCacheManager.builder(connectionFactory)
        .cacheDefaults(config)
        .withInitialCacheConfigurations(cacheConfigs)
        .transactionAware()
        .build();
  }
}
