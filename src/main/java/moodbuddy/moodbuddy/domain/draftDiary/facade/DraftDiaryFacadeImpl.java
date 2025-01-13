package moodbuddy.moodbuddy.domain.draftDiary.facade;

import lombok.RequiredArgsConstructor;
import moodbuddy.moodbuddy.domain.diary.service.DiaryService;
import moodbuddy.moodbuddy.domain.diary.service.image.DiaryImageService;
import moodbuddy.moodbuddy.domain.draftDiary.dto.request.DraftDiaryReqPublishDTO;
import moodbuddy.moodbuddy.domain.draftDiary.dto.request.DraftDiaryReqSaveDTO;
import moodbuddy.moodbuddy.domain.draftDiary.dto.request.DraftDiaryReqSelectDeleteDTO;
import moodbuddy.moodbuddy.domain.draftDiary.dto.response.DraftDiaryResDetailDTO;
import moodbuddy.moodbuddy.domain.draftDiary.dto.response.DraftDiaryResFindOneDTO;
import moodbuddy.moodbuddy.domain.diary.dto.response.save.DiaryResSaveDTO;
import moodbuddy.moodbuddy.domain.draftDiary.dto.response.DraftDiaryResSaveDTO;
import moodbuddy.moodbuddy.domain.draftDiary.service.DraftDiaryService;
import moodbuddy.moodbuddy.domain.draftDiary.service.image.DraftDiaryImageService;
import moodbuddy.moodbuddy.domain.user.service.UserService;
import moodbuddy.moodbuddy.global.redis.service.RedisService;
import moodbuddy.moodbuddy.global.util.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DraftDiaryFacadeImpl implements DraftDiaryFacade {
    private final DraftDiaryService draftDiaryService;
    private final DiaryImageService diaryImageService;
    private final DiaryService diaryService;
    private final DraftDiaryImageService draftDiaryImageService;
    private final UserService userService;
    private final RedisService redisService;

    @Override
    @Transactional
    public DraftDiaryResSaveDTO saveDraftDiary(DraftDiaryReqSaveDTO requestDTO) {
        final var userId = JwtUtil.getUserId();
        var draftDiaryId = draftDiaryService.saveDraftDiary(userId, requestDTO);
        if(requestDTO.diaryImageUrls() != null) {
            draftDiaryImageService.saveAll(draftDiaryId, requestDTO.diaryImageUrls());
        }
        return new DraftDiaryResSaveDTO(draftDiaryId);
    }

    @Override
    @Transactional
    public DiaryResSaveDTO publishDraftDiary(DraftDiaryReqPublishDTO requestDTO) {
        final var userId = JwtUtil.getUserId();
        diaryService.validateExistingDiary(userId, requestDTO.diaryDate());
        var diaryId = draftDiaryService.publishDraftDiary(userId, requestDTO);
        draftDiaryImageService.deleteAll(diaryId);
        if(requestDTO.diaryImageUrls() != null) {
            diaryImageService.saveAll(diaryId, requestDTO.diaryImageUrls());
        }
        checkTodayDiary(userId, requestDTO.diaryDate());
        redisService.deleteDiaryCaches(userId);
        return new DiaryResSaveDTO(diaryId);
    }

    @Override
    public List<DraftDiaryResFindOneDTO> getDraftDiaries() {
        final var userId = JwtUtil.getUserId();
        return draftDiaryService.getDraftDiaries(userId);
    }

    @Override
    @Transactional
    public void deleteDraftDiaries(DraftDiaryReqSelectDeleteDTO requestDTO) {
        final var userId = JwtUtil.getUserId();
        draftDiaryService.deleteDraftDiaries(userId, requestDTO);
    }

    @Override
    public DraftDiaryResDetailDTO getDraftDiary(Long diaryId) {
        final var userId = JwtUtil.getUserId();
        return draftDiaryService.getDraftDiary(userId, diaryId);
    }
    private void checkTodayDiary(Long userId, LocalDate diaryDate) {
        var today = LocalDate.now();
        if (diaryDate.isEqual(today)) {
            userService.changeCount(userId, false);
            userService.setUserCheckTodayDairy(userId, false);
        }
    }
}