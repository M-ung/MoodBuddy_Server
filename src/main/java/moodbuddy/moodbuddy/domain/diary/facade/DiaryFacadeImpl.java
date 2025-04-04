package moodbuddy.moodbuddy.domain.diary.facade;

import lombok.RequiredArgsConstructor;
import moodbuddy.moodbuddy.domain.bookMark.service.BookMarkService;
import moodbuddy.moodbuddy.domain.diary.domain.Diary;
import moodbuddy.moodbuddy.domain.diary.dto.request.save.DiaryReqSaveDTO;
import moodbuddy.moodbuddy.domain.diary.dto.request.update.DiaryReqUpdateDTO;
import moodbuddy.moodbuddy.domain.diary.dto.response.DiaryResDetailDTO;
import moodbuddy.moodbuddy.domain.diary.dto.response.save.DiaryResSaveDTO;
import moodbuddy.moodbuddy.domain.diary.service.image.DiaryImageService;
import moodbuddy.moodbuddy.domain.diary.service.DiaryService;
import moodbuddy.moodbuddy.domain.diary.service.query.DiaryQueryService;
import moodbuddy.moodbuddy.domain.draftDiary.service.DraftDiaryService;
import moodbuddy.moodbuddy.domain.user.service.UserService;
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
    private final DiaryQueryService diaryQueryService;
    private final DraftDiaryService draftDiaryService;
    private final DiaryImageService diaryImageService;
    private final BookMarkService bookMarkService;
    private final UserService userService;

    @Override
    @Transactional
    public DiaryResSaveDTO save(DiaryReqSaveDTO requestDTO) {
        Long userId = JwtUtil.getUserId();
        Diary diary = diaryService.save(userId, requestDTO);
        diaryQueryService.save(diary);
        postSaveOrUpdate(diary.getId(), userId, requestDTO.diaryDate(), requestDTO.diaryImageUrls());
        return new DiaryResSaveDTO(diary.getId());
    }

    @Override
    @Transactional
    public DiaryResSaveDTO update(DiaryReqUpdateDTO requestDTO) {
        Long userId = JwtUtil.getUserId();
        Diary diary = diaryService.update(userId, requestDTO);
        diaryQueryService.update(diary);
        diaryImageService.deleteAll(diary.getId());
        postSaveOrUpdate(diary.getId(), userId, requestDTO.diaryDate(), requestDTO.diaryImageUrls());
        return new DiaryResSaveDTO(diary.getId());
    }

    @Override
    @Transactional
    public void delete(Long diaryId) {
        Long userId = JwtUtil.getUserId();
        LocalDate diaryDate = diaryService.delete(userId, diaryId);
        diaryQueryService.delete(diaryId);
        bookMarkService.deleteByDiaryId(diaryId);
        diaryImageService.deleteAll(diaryId);
        postDelete(userId, diaryDate);
    }

    @Override
    public DiaryResDetailDTO getDiary(Long diaryId) {
        return diaryService.getDiary(JwtUtil.getUserId(), diaryId);
    }

    private void postSaveOrUpdate(Long diaryId, Long userId, LocalDate diaryDate, List<String> imageUrls) {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            diaryImageService.saveAll(diaryId, imageUrls);
        }
        checkToday(diaryDate, userId, false);
        draftDiaryService.deleteByDate(userId, diaryDate);
    }

    private void postDelete(Long userId, LocalDate diaryDate) {
        checkToday(diaryDate, userId, true);
    }

    private void checkToday(LocalDate diaryDate, Long userId, boolean increment) {
        if (LocalDate.now().isEqual(diaryDate)) {
            userService.changeCount(userId, increment);
            userService.setUserCheckTodayDairy(userId, increment);
        }
    }
}