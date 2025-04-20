package moodbuddy.moodbuddy.infra.cache.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class ConcurrentHashMapServiceImpl implements CacheService {
    private final CacheManager cacheManager;
    private static final String DIARIES_CACHE_NAME = "diaries";

    @Override
    public void delete(Long userId) {
        String prefix = "userId:" + userId + "_";
        Cache cache = cacheManager.getCache(DIARIES_CACHE_NAME);
        if (cache instanceof ConcurrentMapCache mapCache) {
            ConcurrentMap<Object, Object> nativeCache = mapCache.getNativeCache();
            nativeCache.keySet().forEach(key -> {
                if (key.toString().startsWith(prefix)) {
                    cache.evict(key);
                }
            });
        }
    }
}