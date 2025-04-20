package moodbuddy.moodbuddy.domain.diary.service.query;

import lombok.RequiredArgsConstructor;
import moodbuddy.moodbuddy.domain.diary.domain.Diary;
import moodbuddy.moodbuddy.domain.diary.domain.DiaryQuery;
import moodbuddy.moodbuddy.domain.diary.domain.type.DiaryEmotion;
import moodbuddy.moodbuddy.domain.diary.dto.request.query.DiaryReqFilterDTO;
import moodbuddy.moodbuddy.domain.diary.dto.response.query.DiaryResQueryDTO;
import moodbuddy.moodbuddy.domain.diary.exception.query.DiaryQueryNotFoundException;
import moodbuddy.moodbuddy.domain.diary.repository.query.DiaryQueryRepository;
import moodbuddy.moodbuddy.global.common.base.PageCustom;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static moodbuddy.moodbuddy.global.error.ErrorCode.DIARY_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryQueryServiceImpl implements DiaryQueryService {
    private final DiaryQueryRepository diaryQueryRepository;
    private final CacheManager cacheManager;


    @Override
    @Cacheable(
            cacheNames = "diaries",
            key = "'userId:' + #userId + '_sort:' + #isAscending + '_page:' + #pageable.pageNumber + '_size:' + #pageable.pageSize",
            unless = "#result == null"
    )
    public PageCustom<DiaryResQueryDTO> getDiaries(final Long userId, boolean isAscending, Pageable pageable) {
        System.out.println("CacheManager class = " + cacheManager.getClass().getName());
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
    @Transactional
    public void save(final Diary diary) {
        diaryQueryRepository.save(DiaryQuery.from(diary));
    }

    @Override
    @Transactional
    public void update(final Diary diary) {
        findDiaryQueryById(diary.getId()).update(diary);
    }

    @Override
    @Transactional
    public void delete(final Long diaryId) {
        diaryQueryRepository.deleteById(diaryId);
    }

    private DiaryQuery findDiaryQueryById(final Long diaryId) {
        return diaryQueryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryQueryNotFoundException(DIARY_NOT_FOUND));
    }
}