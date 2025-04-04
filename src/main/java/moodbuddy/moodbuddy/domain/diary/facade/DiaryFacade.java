package moodbuddy.moodbuddy.domain.diary.facade;

import moodbuddy.moodbuddy.domain.diary.dto.request.save.DiaryReqSaveDTO;
import moodbuddy.moodbuddy.domain.diary.dto.request.update.DiaryReqUpdateDTO;
import moodbuddy.moodbuddy.domain.diary.dto.response.DiaryResDetailDTO;
import moodbuddy.moodbuddy.domain.diary.dto.response.save.DiaryResSaveDTO;

public interface DiaryFacade {
    DiaryResSaveDTO save(DiaryReqSaveDTO requestDTO);
    DiaryResSaveDTO update(DiaryReqUpdateDTO requestDTO);
    void delete(final Long diaryId);
    DiaryResDetailDTO getDiary(final Long diaryId);
}
