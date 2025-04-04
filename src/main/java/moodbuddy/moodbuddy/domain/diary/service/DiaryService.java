package moodbuddy.moodbuddy.domain.diary.service;

import moodbuddy.moodbuddy.domain.diary.domain.Diary;
import moodbuddy.moodbuddy.domain.diary.dto.request.save.DiaryReqSaveDTO;
import moodbuddy.moodbuddy.domain.diary.dto.request.update.DiaryReqUpdateDTO;
import moodbuddy.moodbuddy.domain.diary.dto.response.DiaryResDetailDTO;
import java.time.LocalDate;

public interface DiaryService {
    Diary save(final Long userId, DiaryReqSaveDTO diaryReqSaveDTO);
    Diary update(final Long userId, DiaryReqUpdateDTO diaryReqUpdateDTO);
    LocalDate delete(final Long userId, final Long diaryId);
    DiaryResDetailDTO getDiary(final Long userId, final Long diaryId);
    Diary findDiaryById(final Long userId, final Long diaryId);
}
