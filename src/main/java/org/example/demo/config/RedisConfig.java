package org.example.demo.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {

  /**
   * ✅ ObjectMapper WITH type info - used ONLY for Redis cache storage
   * Uses Jackson 2.x because Spring Data Redis 4.0 still uses com.fasterxml.jackson
   */
  private ObjectMapper createRedisObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.activateDefaultTyping(
        LaissezFaireSubTypeValidator.instance,
        ObjectMapper.DefaultTyping.NON_FINAL,
        JsonTypeInfo.As.WRAPPER_ARRAY);
    return mapper;
  }



  /**
   * Prevent stale/legacy Redis payloads from breaking API requests.
   *
   * If a cache read fails (for example an old entry without @class metadata),
   * evict that key and allow Spring to execute the method and repopulate cache.
   */
  @Bean
  public CacheErrorHandler cacheErrorHandler() {
    return new CacheErrorHandler() {
      @Override
      public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        if (exception instanceof SerializationException) {
          log.warn("Redis serialization error for cache='{}', key='{}'. Evicting stale entry...",
              cache.getName(), key);
          try {
            cache.evict(key);
          } catch (Exception e) {
            log.error("Failed to evict stale cache key: {}", key, e);
          }
          return;
        }
        log.error("Cache get error for cache='{}', key=''", cache.getName(), key, exception);
        throw exception;
      }

      @Override
      public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        log.error("Cache put error for cache='{}', key=''", cache.getName(), key, exception);
        throw exception;
      }

      @Override
      public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.error("Cache evict error for cache='{}', key=''", cache.getName(), key, exception);
        throw exception;
      }

      @Override
      public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.error("Cache clear error for cache='{}'", cache.getName(), exception);
        throw exception;
      }
    };
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    GenericJackson2JsonRedisSerializer serializer =
        new GenericJackson2JsonRedisSerializer(createRedisObjectMapper());

    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(serializer);
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(serializer);

    return template;
  }

  @Bean
  public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    GenericJackson2JsonRedisSerializer serializer =
        new GenericJackson2JsonRedisSerializer(createRedisObjectMapper());

    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofMinutes(30))
        .serializeKeysWith(
            RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(serializer))
        .disableCachingNullValues();

    return RedisCacheManager.builder(connectionFactory)
        .cacheDefaults(config)
        .build();
  }
}
