package moodbuddy.moodbuddy.domain.bookMark.facade;

import moodbuddy.moodbuddy.domain.diary.dto.response.DiaryResDetailDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookMarkFacade {
    boolean toggle(final Long diaryId);
    Page<DiaryResDetailDTO> bookMarkFindAllByWithPageable(Pageable pageable);
}