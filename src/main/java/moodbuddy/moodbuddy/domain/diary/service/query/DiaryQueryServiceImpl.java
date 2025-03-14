package moodbuddy.moodbuddy.domain.diary.service.query;

import lombok.RequiredArgsConstructor;
import moodbuddy.moodbuddy.domain.diary.domain.type.DiaryEmotion;
import moodbuddy.moodbuddy.domain.diary.dto.request.query.DiaryReqFilterDTO;
import moodbuddy.moodbuddy.domain.diary.dto.response.query.DiaryResQueryDTO;
import moodbuddy.moodbuddy.domain.diary.repository.query.DiaryQueryRepository;
import moodbuddy.moodbuddy.global.common.base.PageCustom;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryQueryServiceImpl implements DiaryQueryService {
    private final DiaryQueryRepository diaryQueryRepository;

    @Override
    @Cacheable(
            cacheNames = "diaries",
            key = "'userId:' + #userId + '_sort:' + #isAscending + '_page:' + #pageable.pageNumber + '_size:' + #pageable.pageSize",
            unless = "#result == null"
    )
    public PageCustom<DiaryResQueryDTO> getDiaries(final Long userId, boolean isAscending, Pageable pageable) {
        return diaryQueryRepository.findDiariesWithPageable(userId, isAscending, pageable);
    }

    @Override
    public PageCustom<DiaryResQueryDTO> getDiariesByEmotion(final Long userId, boolean isAscending, DiaryEmotion diaryEmotion, Pageable pageable) {
        return diaryQueryRepository.findDiariesByEmotionWithPageable(userId, isAscending, diaryEmotion, pageable);
    }

    @Override
    public PageCustom<DiaryResQueryDTO> getDiariesByFilter(final Long userId, boolean isAscending, DiaryReqFilterDTO requestDTO, Pageable pageable) {
        return diaryQueryRepository.findDiariesByFilterWithPageable(userId, isAscending, requestDTO, pageable);
    }

    @Override
    @Cacheable(
            cacheNames = "diaries",
            key = "'userId:' + #userId + '_sort:' + #isAscending + '_page:' + #pageable.pageNumber + '_size:' + #pageable.pageSize",
            unless = "#result == null"
    )
    public PageCustom<DiaryResQueryDTO> refreshDiariesCache(final Long userId, boolean isAscending, Pageable pageable) {
        return diaryQueryRepository.findDiariesWithPageable(userId, false, pageable);
    }
}