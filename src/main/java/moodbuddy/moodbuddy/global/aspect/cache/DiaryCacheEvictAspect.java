package moodbuddy.moodbuddy.global.aspect.cache;

import lombok.RequiredArgsConstructor;
import moodbuddy.moodbuddy.infra.cache.service.CacheService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class DiaryCacheEvictAspect {
    private final CacheService cacheService;

    @AfterReturning("@annotation(moodbuddy.moodbuddy.global.annotation.DiaryCacheEvict)")
    public void diaryEvictCache(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals("userId") && args[i] instanceof Long userId) {
                cacheService.delete(userId);
                break;
            }
        }
    }
}