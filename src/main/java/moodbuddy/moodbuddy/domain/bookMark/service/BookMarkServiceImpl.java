package moodbuddy.moodbuddy.domain.bookMark.service;

import lombok.RequiredArgsConstructor;
import moodbuddy.moodbuddy.domain.bookMark.domain.BookMark;
import moodbuddy.moodbuddy.domain.bookMark.repository.BookMarkRepository;
import moodbuddy.moodbuddy.domain.diary.domain.Diary;
import moodbuddy.moodbuddy.domain.diary.dto.response.query.DiaryResQueryDTO;
import moodbuddy.moodbuddy.global.common.base.PageCustom;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookMarkServiceImpl implements BookMarkService {
    private final BookMarkRepository bookMarkRepository;

    @Transactional
    @Override
    public boolean toggle(Diary diary, final Long userId) {
        var findBookMark = bookMarkRepository.findByUserIdAndDiaryId(userId, diary.getId());
        return findBookMark.map(bookMark -> cancelToggle(diary, bookMark)).orElseGet(() -> saveToggle(diary, userId));
    }

    private boolean saveToggle(Diary diary, Long userId) {
        diary.updateDiaryBookMarkCheck(true);
        var bookMark = BookMark.of(userId, diary.getId());
        bookMarkRepository.save(bookMark);
        return true;
    }

    private boolean cancelToggle(Diary diary, BookMark bookMark) {
        bookMarkRepository.delete(bookMark);
        diary.updateDiaryBookMarkCheck(false);
        return false;
    }

    @Override
    public PageCustom<DiaryResQueryDTO> getBookMarks(Pageable pageable, final Long userId) {
        return bookMarkRepository.getBookMarksWithPageable(userId, pageable);
    }

    @Override
    @Transactional
    public void deleteByDiaryId(Long diaryId) {
        var findBookMark = bookMarkRepository.findByDiaryId(diaryId);
        if(findBookMark.isPresent()) {
            bookMarkRepository.deleteByDiaryId(diaryId);
        }
    }
}