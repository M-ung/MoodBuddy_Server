package moodbuddy.moodbuddy.domain.bookMark.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moodbuddy.moodbuddy.domain.bookMark.dto.response.BookMarkResToggleDTO;
import moodbuddy.moodbuddy.domain.bookMark.entity.BookMark;
import moodbuddy.moodbuddy.domain.bookMark.repository.BookMarkRepository;
import moodbuddy.moodbuddy.domain.diary.dto.response.DiaryResDetailDTO;
import moodbuddy.moodbuddy.domain.diary.entity.Diary;
import moodbuddy.moodbuddy.domain.diary.service.DiaryFindService;
import moodbuddy.moodbuddy.domain.diary.service.DiaryService;
import moodbuddy.moodbuddy.domain.diary.service.DiaryServiceImpl;
import moodbuddy.moodbuddy.domain.diary.util.DiaryUtil;
import moodbuddy.moodbuddy.domain.diaryImage.service.DiaryImageServiceImpl;
import moodbuddy.moodbuddy.domain.user.entity.User;
import moodbuddy.moodbuddy.domain.user.service.UserService;
import moodbuddy.moodbuddy.domain.user.service.UserServiceImpl;
import moodbuddy.moodbuddy.global.common.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BookMarkServiceImpl implements BookMarkService {
    private final BookMarkRepository bookMarkRepository;
    private final UserService userService;
    private final DiaryFindService diaryFindService;

    @Override
    @Transactional
    public BookMarkResToggleDTO toggle(Long diaryId) {
        log.info("[BookMarkServiceImpl] toggle");
        final Long kakaoId = JwtUtil.getUserId();

        final User findUser = userService.findUserByKakaoId(kakaoId);
        final Diary findDiary = diaryFindService.findDiaryById(diaryId);

        diaryFindService.validateDiaryAccess(findDiary, kakaoId);

        Optional<BookMark> optionalBookMark = bookMarkRepository.findByUserAndDiary(findUser, findDiary);

        if(optionalBookMark.isPresent()) { // 북마크가 존재한다면,
            // 북마크 취소
            bookMarkRepository.delete(optionalBookMark.get());
            findDiary.setDiaryBookMarkCheck(false);
            return new BookMarkResToggleDTO(false);
        } else { // 북마크가 존재하지 않는다면,
            // 북마크 저장
            BookMark newBookMark = BookMark.builder()
                    .user(findUser)
                    .diary(findDiary)
                    .build();
            findDiary.setDiaryBookMarkCheck(true);
            bookMarkRepository.save(newBookMark);
            return new BookMarkResToggleDTO(true);
        }
    }

    @Override
    public Page<DiaryResDetailDTO> bookMarkFindAllByWithPageable(Pageable pageable) {
        log.info("[BookMarkServiceImpl] bookMarkFindAllByWithPageable");
        final Long kakaoId = JwtUtil.getUserId();
        final User findUser = userService.findUserByKakaoId(kakaoId);

        return bookMarkRepository.bookMarkFindAllWithPageable(findUser, pageable);
    }

    @Override
    public void deleteByDiaryId(Long diaryId) {
        Optional<BookMark> optionalBookMark = bookMarkRepository.findByDiaryId(diaryId);
        if(optionalBookMark.isPresent()) {
            bookMarkRepository.deleteByDiaryId(diaryId);
        }
    }
}
