package moodbuddy.moodbuddy.domain.draftDiary.service;

import moodbuddy.moodbuddy.domain.draftDiary.dto.request.DraftDiaryReqPublishDTO;
import moodbuddy.moodbuddy.domain.draftDiary.dto.request.DraftDiaryReqSaveDTO;
import moodbuddy.moodbuddy.domain.draftDiary.dto.request.DraftDiaryReqSelectDeleteDTO;
import moodbuddy.moodbuddy.domain.draftDiary.dto.response.DraftDiaryResDetailDTO;
import moodbuddy.moodbuddy.domain.draftDiary.dto.response.DraftDiaryResFindOneDTO;
import java.time.LocalDate;
import java.util.List;

public interface DraftDiaryService {
    Long save(final Long userId, DraftDiaryReqSaveDTO diaryReqSaveDTO);
    Long publish(final Long userId, DraftDiaryReqPublishDTO requestDTO);
    List<DraftDiaryResFindOneDTO> getDraftDiaries(final Long userId);
    void delete(final Long userId, DraftDiaryReqSelectDeleteDTO requestDTO);
    DraftDiaryResDetailDTO getDraftDiary(final Long userId,  final Long diaryId);
    void deleteByDate(final Long userId, LocalDate draftDiaryDate);
}