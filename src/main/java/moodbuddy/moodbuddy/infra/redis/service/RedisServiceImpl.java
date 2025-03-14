package moodbuddy.moodbuddy.infra.redis.service;

import lombok.RequiredArgsConstructor;
import moodbuddy.moodbuddy.domain.diary.service.query.DiaryQueryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final DiaryQueryService diaryQueryService;
    private static final String DIARIES_CACHE_PREFIX = "diaries::userId:";
    private static final String DIARY_COUNT_CACHE_PREFIX = "diary_count:userId:";
    private static final int PAGE_SIZE = 20;

    @Override
    public void deleteDiaryCaches(Long userId) {
        deleteCacheByUserIdAndCacheName(userId);
        deleteCountCacheByUserId(userId);
        cacheFirstPage(userId);
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

    private void cacheFirstPage(Long userId) {
        diaryQueryService.refreshDiariesCache(userId, false, PageRequest.of(0, PAGE_SIZE));
    }
}