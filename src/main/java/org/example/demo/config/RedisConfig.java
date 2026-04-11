package org.example.demo.config;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
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
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {

  /**
   * Jackson 3 JsonMapper for Redis — no polymorphic typing needed.
   * Type info is handled explicitly by TypeAwareRedisSerializer.
   */
  private ObjectMapper createRedisObjectMapper() {
    return JsonMapper.builder().build();
  }

  /**
   * Graceful error handler for cache serialization issues
   */
  @Bean
  public CacheErrorHandler cacheErrorHandler() {
    return new CacheErrorHandler() {
      @Override
      public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.warn("Redis serialization error on cache='{}', key='{}'. Evicting stale entry...",
                cache.getName(), key);
        try {
          cache.evict(key);
        } catch (Exception e) {
          log.error("Failed to evict stale key: {}", key, e);
        }
      }

      @Override
      public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        log.error("Cache put error for cache='{}', key='{}'", cache.getName(), key, exception);
        throw exception;
      }

      @Override
      public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.error("Cache evict error for cache='{}', key='{}'", cache.getName(), key, exception);
        throw exception;
      }

      @Override
      public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.error("Cache clear error for cache='{}'", cache.getName(), exception);
        throw exception;
      }
    };
  }

  /**
   * Custom RedisSerializer that stores the fully-qualified class name as a prefix,
   * then the JSON payload. Format: "className|{json...}"
   * This avoids Jackson's unreliable polymorphic type metadata.
   */
  private static class TypeAwareRedisSerializer implements RedisSerializer<Object> {
    private final ObjectMapper mapper;

    TypeAwareRedisSerializer(ObjectMapper mapper) {
      this.mapper = mapper;
    }

    @Override
    public byte[] serialize(Object value) throws SerializationException {
      if (value == null) return null;
      try {
        String json = mapper.writeValueAsString(value);
        String payload = value.getClass().getName() + "|" + json;
        return payload.getBytes(StandardCharsets.UTF_8);
      } catch (Exception e) {
        throw new SerializationException("Failed to serialize", e);
      }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
      if (bytes == null) return null;
      try {
        String payload = new String(bytes, StandardCharsets.UTF_8);
        int sep = payload.indexOf('|');
        if (sep < 0) throw new SerializationException("Invalid payload: no class separator");
        String className = payload.substring(0, sep);
        String json = payload.substring(sep + 1);
        Class<?> clazz = Class.forName(className);
        return mapper.readerFor(clazz).readValue(json);
      } catch (ClassNotFoundException e) {
        throw new SerializationException("Class not found", e);
      } catch (Exception e) {
        throw new SerializationException("Failed to deserialize", e);
      }
    }
  }

  /**
   * RedisTemplate for manual operations
   */
  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    RedisSerializer<Object> valueSerializer = new TypeAwareRedisSerializer(createRedisObjectMapper());

    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(valueSerializer);
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(valueSerializer);

    template.afterPropertiesSet();
    return template;
  }

  /**
   * RedisCacheManager with different TTL per cache
   */
  @Bean
  public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    RedisSerializer<Object> valueSerializer = new TypeAwareRedisSerializer(createRedisObjectMapper());

    RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))
            .disableCachingNullValues();

    Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
    cacheConfigs.put("series:list",    defaultConfig.entryTtl(Duration.ofHours(1)));
    cacheConfigs.put("series:detail",  defaultConfig.entryTtl(Duration.ofMinutes(45)));
    cacheConfigs.put("episode:stream", defaultConfig.entryTtl(Duration.ofMinutes(15)));
    cacheConfigs.put("user:library",   defaultConfig.entryTtl(Duration.ofMinutes(25)));
    cacheConfigs.put("user:progress",  defaultConfig.entryTtl(Duration.ofMinutes(10)));

    return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(cacheConfigs)
            .transactionAware()
            .build();
  }
}