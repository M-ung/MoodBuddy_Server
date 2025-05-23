package moodbuddy.moodbuddy.domain.bookMark.facade;

import lombok.RequiredArgsConstructor;
import moodbuddy.moodbuddy.domain.bookMark.service.BookMarkService;
import moodbuddy.moodbuddy.domain.diary.dto.response.query.DiaryResQueryDTO;
import moodbuddy.moodbuddy.domain.diary.service.DiaryService;
import moodbuddy.moodbuddy.global.common.base.PageCustom;
import moodbuddy.moodbuddy.global.util.JwtUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookMarkFacadeImpl implements BookMarkFacade {
    private final BookMarkService bookMarkService;
    private final DiaryService diaryService;

    @Override
    @Transactional
    public boolean toggle(final Long diaryId) {
        final var userId = JwtUtil.getUserId();
        var findDiary = diaryService.findDiaryById(userId, diaryId);
        return bookMarkService.toggle(findDiary, userId);
    }

    @Override
    public PageCustom<DiaryResQueryDTO> getBookMarks(Pageable pageable) {
        final var userId = JwtUtil.getUserId();
        return bookMarkService.getBookMarks(pageable, userId);
    }
}