package moodbuddy.moodbuddy.domain.draftDiary.service;

import lombok.RequiredArgsConstructor;
import moodbuddy.moodbuddy.domain.diary.domain.Diary;
import moodbuddy.moodbuddy.domain.diary.exception.DiaryTodayExistingException;
import moodbuddy.moodbuddy.domain.diary.repository.DiaryRepository;
import moodbuddy.moodbuddy.domain.draftDiary.domain.DraftDiary;
import moodbuddy.moodbuddy.domain.draftDiary.dto.request.DraftDiaryReqPublishDTO;
import moodbuddy.moodbuddy.domain.draftDiary.dto.request.DraftDiaryReqSaveDTO;
import moodbuddy.moodbuddy.domain.draftDiary.dto.request.DraftDiaryReqSelectDeleteDTO;
import moodbuddy.moodbuddy.domain.draftDiary.dto.response.DraftDiaryResDetailDTO;
import moodbuddy.moodbuddy.domain.draftDiary.dto.response.DraftDiaryResFindOneDTO;
import moodbuddy.moodbuddy.domain.draftDiary.repository.DraftDiaryRepository;
import moodbuddy.moodbuddy.global.annotation.DiaryCacheEvict;
import moodbuddy.moodbuddy.global.common.base.type.MoodBuddyStatus;
import moodbuddy.moodbuddy.global.error.ErrorCode;
import moodbuddy.moodbuddy.domain.draftDiary.exception.DraftDiaryConcurrentUpdateException;
import moodbuddy.moodbuddy.domain.draftDiary.exception.DraftDiaryNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import static moodbuddy.moodbuddy.global.error.ErrorCode.DIARY_TODAY_EXISTING;
import static moodbuddy.moodbuddy.global.error.ErrorCode.DRAFT_DIARY_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DraftDiaryServiceImpl implements DraftDiaryService {
    private final DraftDiaryRepository draftDiaryRepository;
    private final DiaryRepository diaryRepository;

    @Override
    @Transactional
    public Long save(final Long userId, DraftDiaryReqSaveDTO requestDTO) {
        return draftDiaryRepository.save((DraftDiary.of(
                requestDTO,
                userId))).getId();
    }

    @Override
    @Transactional
    @DiaryCacheEvict
    public Long publish(final Long userId, DraftDiaryReqPublishDTO requestDTO) {
        try {
            var findDraftDiary = findDraftDiaryById(userId, requestDTO.diaryId());
            findDraftDiary.validateDraftDiaryAccess(userId);
            deleteByDate(userId, findDraftDiary.getDate());
            return diaryRepository.save(Diary.publish(userId, requestDTO)).getId();
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new DraftDiaryConcurrentUpdateException(ErrorCode.DRAFT_DIARY_CONCURRENT_UPDATE);
        } catch (DataIntegrityViolationException e) {
            throw new DiaryTodayExistingException(DIARY_TODAY_EXISTING);
        }
    }

    @Override
    public List<DraftDiaryResFindOneDTO> getDraftDiaries(final Long userId) {
        return draftDiaryRepository.getDraftDiaries(userId);
    }

    @Override
    @Transactional
    public void delete(final Long userId, DraftDiaryReqSelectDeleteDTO requestDTO) {
        try {
            requestDTO.diaryIdList().forEach(draftDiaryId ->
                    findDraftDiaryById(userId, draftDiaryId).updateMoodBuddyStatus(MoodBuddyStatus.DIS_ACTIVE));
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new DraftDiaryConcurrentUpdateException(ErrorCode.DRAFT_DIARY_CONCURRENT_DELETE);
        }
    }

    @Override
    public DraftDiaryResDetailDTO getDraftDiary(final Long userId, final Long diaryId) {
        return draftDiaryRepository.getDraftDiaryById(userId, diaryId);
    }

    @Override
    public void deleteByDate(final Long userId, LocalDate draftDiaryDate) {
        draftDiaryRepository.findAllByUserIdAndDate(userId, draftDiaryDate)
                .forEach(draftDiary -> draftDiary.updateMoodBuddyStatus(MoodBuddyStatus.DIS_ACTIVE));
    }

    private DraftDiary findDraftDiaryById(final Long userId, final Long draftDiaryId) {
        return draftDiaryRepository.findByUserIdAndIdAndMoodBuddyStatus(userId, draftDiaryId, MoodBuddyStatus.ACTIVE)
                .orElseThrow(() -> new DraftDiaryNotFoundException(DRAFT_DIARY_NOT_FOUND));
    }
}