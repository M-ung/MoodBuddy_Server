package moodbuddy.moodbuddy.infra.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer;

@Configuration
@EnableCaching
public class RedisCacheConfig {
    private ObjectMapper objectMapper() {
        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Object.class)
                .build();
        return new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT) // Json 들여쓰기 출력
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // 날짜를 문자열로 저장
                .registerModule(new JavaTimeModule()) // Java 8 날짜 타입
                .activateDefaultTyping(typeValidator, ObjectMapper.DefaultTyping.NON_FINAL); // 다양한 타입의 객체를 안전하게 Json으로 저장
    }

    private RedisCacheConfiguration defaultCacheConfiguration() {
        return RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues() // Null은 캐시 X
                .serializeKeysWith(fromSerializer(new StringRedisSerializer())) // Key는 문자열로 저장
                .serializeValuesWith(fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper()))); // 값은 Json으로 저장
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory cf) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("diaries", defaultCacheConfiguration().entryTtl(Duration.ofHours(24 + (int) (Math.random() * 5))));
        return RedisCacheManager.builder(cf)
                .cacheDefaults(defaultCacheConfiguration())
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}