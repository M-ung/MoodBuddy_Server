package moodbuddy.moodbuddy.domain.diary.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moodbuddy.moodbuddy.domain.bookMark.service.BookMarkService;
import moodbuddy.moodbuddy.domain.diary.dto.request.save.DiaryReqSaveDTO;
import moodbuddy.moodbuddy.domain.diary.dto.request.update.DiaryReqUpdateDTO;
import moodbuddy.moodbuddy.domain.diary.dto.response.DiaryResDetailDTO;
import moodbuddy.moodbuddy.domain.diary.dto.response.save.DiaryResSaveDTO;
import moodbuddy.moodbuddy.domain.diary.service.image.DiaryImageService;
import moodbuddy.moodbuddy.domain.diary.service.DiaryService;
import moodbuddy.moodbuddy.domain.draftDiary.service.DraftDiaryService;
import moodbuddy.moodbuddy.domain.user.service.UserService;
import moodbuddy.moodbuddy.infra.redis.service.RedisService;
import moodbuddy.moodbuddy.global.util.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryFacadeImpl implements DiaryFacade {
    private final DiaryService diaryService;
    private final DraftDiaryService draftDiaryService;
    private final DiaryImageService diaryImageService;
    private final BookMarkService bookMarkService;
    private final UserService userService;
    private final RedisService redisService;

    @Override
    @Transactional
    public DiaryResSaveDTO saveDiary(DiaryReqSaveDTO requestDTO) {
        final var userId = JwtUtil.getUserId();
        diaryService.validateExistingDiary(userId, requestDTO.diaryDate());
        var diaryId = diaryService.saveDiary(userId, requestDTO);
        saveDiaryImages(diaryId, requestDTO.diaryImageUrls());
        checkTodayDiary(userId, requestDTO.diaryDate(), false);
        deleteData(userId, requestDTO.diaryDate());
        return new DiaryResSaveDTO(diaryId);
    }

    @Override
    @Transactional
    public DiaryResSaveDTO updateDiary(DiaryReqUpdateDTO requestDTO) {
        final var userId = JwtUtil.getUserId();
        var diaryId = diaryService.updateDiary(userId, requestDTO);
        diaryImageService.deleteAll(diaryId);
        saveDiaryImages(diaryId, requestDTO.diaryImageUrls());
        deleteData(userId, requestDTO.diaryDate());
        return new DiaryResSaveDTO(diaryId);
    }

    @Override
    @Transactional
    public void deleteDiary(final Long diaryId) {
        final var userId = JwtUtil.getUserId();
        var diaryDate = diaryService.deleteDiary(userId, diaryId);
        bookMarkService.deleteByDiaryId(diaryId);
        diaryImageService.deleteAll(diaryId);
        checkTodayDiary(userId, diaryDate, true);
        redisService.deleteDiaryCaches(userId);
    }

    @Override
    public DiaryResDetailDTO getDiary(final Long diaryId) {
        final var userId = JwtUtil.getUserId();
        return diaryService.getDiary(userId, diaryId);
    }

    private void checkTodayDiary(final Long userId, LocalDate diaryDate, boolean check) {
        var today = LocalDate.now();
        if (diaryDate.isEqual(today)) {
            userService.changeCount(userId, check);
            userService.setUserCheckTodayDairy(userId, check);
        }
    }

    private void deleteData(final Long userId, LocalDate date) {
        draftDiaryService.deleteDraftDiariesByDate(userId, date);
        redisService.deleteDiaryCaches(userId);
    }

    private void saveDiaryImages(Long diaryId, List<String> requestDTO) {
        if (requestDTO != null) {
            diaryImageService.saveAll(diaryId, requestDTO);
        }
    }
}