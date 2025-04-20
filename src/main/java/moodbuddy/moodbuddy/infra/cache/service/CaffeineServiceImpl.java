package moodbuddy.moodbuddy.infra.cache.service;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
@RequiredArgsConstructor
public class CaffeineServiceImpl implements CacheService {
    private final CacheManager cacheManager;

    @Override
    public void delete(Long userId) {
        org.springframework.cache.Cache cache = cacheManager.getCache("diaries");

        if (cache instanceof CaffeineCache caffeineCache) {
            Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
            nativeCache.asMap().keySet().removeIf(key ->
                    key.toString().startsWith("userId:" + userId)
            );
        }
    }
}