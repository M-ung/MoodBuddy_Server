package moodbuddy.moodbuddy.domain.diary.service.query;

import moodbuddy.moodbuddy.domain.diary.domain.type.DiaryEmotion;
import moodbuddy.moodbuddy.domain.diary.dto.request.query.DiaryReqFilterDTO;
import moodbuddy.moodbuddy.domain.diary.dto.response.query.DiaryResQueryDTO;
import moodbuddy.moodbuddy.global.common.base.PageCustom;
import org.springframework.data.domain.Pageable;

public interface DiaryQueryService {
    PageCustom<DiaryResQueryDTO> getDiaries(final Long userId, boolean isAscending, Pageable pageable);
    PageCustom<DiaryResQueryDTO> getDiariesByEmotion(final Long userId, boolean isAscending, DiaryEmotion diaryEmotion, Pageable pageable);
    PageCustom<DiaryResQueryDTO> getDiariesByFilter(final Long userId, boolean isAscending, DiaryReqFilterDTO requestDTO, Pageable pageable);
    PageCustom<DiaryResQueryDTO> refreshDiariesCache(final Long userId, boolean isAscending, Pageable pageable);
}
