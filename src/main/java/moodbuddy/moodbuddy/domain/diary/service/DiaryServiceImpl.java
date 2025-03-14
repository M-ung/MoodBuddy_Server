package moodbuddy.moodbuddy.domain.diary.service;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import moodbuddy.moodbuddy.domain.diary.domain.Diary;
import moodbuddy.moodbuddy.domain.diary.dto.request.save.DiaryReqSaveDTO;
import moodbuddy.moodbuddy.domain.diary.dto.request.update.DiaryReqUpdateDTO;
import moodbuddy.moodbuddy.domain.diary.dto.response.DiaryResDetailDTO;
import moodbuddy.moodbuddy.domain.diary.repository.DiaryRepository;
import moodbuddy.moodbuddy.global.common.base.type.MoodBuddyStatus;
import moodbuddy.moodbuddy.global.error.ErrorCode;
import moodbuddy.moodbuddy.domain.diary.exception.DiaryConcurrentUpdateException;
import moodbuddy.moodbuddy.domain.diary.exception.DiaryNotFoundException;
import moodbuddy.moodbuddy.domain.diary.exception.DiaryTodayExistingException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import static moodbuddy.moodbuddy.global.error.ErrorCode.DIARY_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {
    private final DiaryRepository diaryRepository;

    @Override
    @Transactional
    public Long saveDiary(final Long userId, DiaryReqSaveDTO requestDTO) {
        return diaryRepository.save(Diary.of(
                requestDTO,
                userId)).getId();
    }

    @Override
    @Transactional
    public Long updateDiary(final Long userId,  DiaryReqUpdateDTO requestDTO) {
        try {
            var findDiary = findDiaryById(userId, requestDTO.diaryId());
            findDiary.updateDiary(requestDTO);
            return findDiary.getId();
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new DiaryConcurrentUpdateException(ErrorCode.DIARY_CONCURRENT_UPDATE);
        }
    }

    @Override
    @Transactional
    public LocalDate deleteDiary(final Long userId,  final Long diaryId) {
        try {
            final var findDiary = findDiaryById(userId, diaryId);
            findDiary.updateMoodBuddyStatus(MoodBuddyStatus.DIS_ACTIVE);
            return findDiary.getDate();
        } catch (OptimisticLockException ex) {
            throw new DiaryConcurrentUpdateException(ErrorCode.DIARY_CONCURRENT_DELETE);
        }
    }

    @Override
    public DiaryResDetailDTO getDiary(final Long userId, final Long diaryId) {
        return diaryRepository.getDiaryById(userId, diaryId);
    }

    @Override
    public void validateExistingDiary(final Long userId,  LocalDate diaryDate) {
        if (diaryRepository.existsByUserIdAndDate(userId, diaryDate)) {
            throw new DiaryTodayExistingException(ErrorCode.DIARY_TODAY_EXISTING);
        }
    }

    @Override
    public Diary findDiaryById(final Long userId, final Long diaryId) {
        return diaryRepository.findByUserIdAndIdAndMoodBuddyStatus(userId, diaryId, MoodBuddyStatus.ACTIVE)
                .orElseThrow(() -> new DiaryNotFoundException(DIARY_NOT_FOUND));
    }
}