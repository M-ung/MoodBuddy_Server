package moodbuddy.moodbuddy.domain.diary.service;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import moodbuddy.moodbuddy.domain.diary.domain.Diary;
import moodbuddy.moodbuddy.domain.diary.dto.request.save.DiaryReqSaveDTO;
import moodbuddy.moodbuddy.domain.diary.dto.request.update.DiaryReqUpdateDTO;
import moodbuddy.moodbuddy.domain.diary.dto.response.DiaryResDetailDTO;
import moodbuddy.moodbuddy.domain.diary.exception.DiaryTodayExistingException;
import moodbuddy.moodbuddy.domain.diary.exception.image.DiaryConcurrentDeleteException;
import moodbuddy.moodbuddy.domain.diary.repository.DiaryRepository;
import moodbuddy.moodbuddy.global.common.base.type.MoodBuddyStatus;
import moodbuddy.moodbuddy.global.error.ErrorCode;
import moodbuddy.moodbuddy.domain.diary.exception.DiaryConcurrentUpdateException;
import moodbuddy.moodbuddy.domain.diary.exception.DiaryNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import static moodbuddy.moodbuddy.global.error.ErrorCode.DIARY_NOT_FOUND;
import static moodbuddy.moodbuddy.global.error.ErrorCode.DIARY_TODAY_EXISTING;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {
    private final DiaryRepository diaryRepository;

    @Override
    @Transactional
    @CacheEvict(cacheNames = "diaries", allEntries = true)
    public Diary save(final Long userId, DiaryReqSaveDTO requestDTO) {
        try{
            return diaryRepository.save(Diary.of(
                    requestDTO,
                    userId));
        } catch (DataIntegrityViolationException e) {
            throw new DiaryTodayExistingException(DIARY_TODAY_EXISTING);
        }
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "diaries", allEntries = true)
    public Diary update(final Long userId, DiaryReqUpdateDTO requestDTO) {
        try {
            var findDiary = findDiaryById(userId, requestDTO.diaryId());
            findDiary.updateDiary(requestDTO);
            return findDiary;
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new DiaryConcurrentUpdateException(ErrorCode.DIARY_CONCURRENT_UPDATE);
        }
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "diaries", allEntries = true)
    public LocalDate delete(final Long userId, final Long diaryId) {
        try {
            final var findDiary = findDiaryById(userId, diaryId);
            findDiary.updateMoodBuddyStatus(MoodBuddyStatus.DIS_ACTIVE);
            return findDiary.getDate();
        } catch (OptimisticLockException ex) {
            throw new DiaryConcurrentDeleteException(ErrorCode.DIARY_CONCURRENT_DELETE);
        }
    }

    @Override
    public DiaryResDetailDTO getDiary(final Long userId, final Long diaryId) {
        return diaryRepository.getDiaryById(userId, diaryId);
    }

    @Override
    public Diary findDiaryById(final Long userId, final Long diaryId) {
        return diaryRepository.findByUserIdAndIdAndMoodBuddyStatus(userId, diaryId, MoodBuddyStatus.ACTIVE)
                .orElseThrow(() -> new DiaryNotFoundException(DIARY_NOT_FOUND));
    }
}