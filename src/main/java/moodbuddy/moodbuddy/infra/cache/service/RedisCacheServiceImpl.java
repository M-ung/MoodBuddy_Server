package moodbuddy.moodbuddy.infra.cache.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisCacheServiceImpl implements CacheService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String DIARIES_CACHE_PREFIX = "diaries::userId:";
    private static final String DIARY_COUNT_CACHE_PREFIX = "diary_count:userId:";

    @Override
    public void delete(Long userId) {
        deleteCacheByUserIdAndCacheName(userId);
        deleteCountCacheByUserId(userId);
    }

    private void deleteCacheByUserIdAndCacheName(Long userId) {
        var pattern = DIARIES_CACHE_PREFIX + userId + "*";
        var keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    private void deleteCountCacheByUserId(Long userId) {
        String pattern = DIARY_COUNT_CACHE_PREFIX + userId;
        var keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}