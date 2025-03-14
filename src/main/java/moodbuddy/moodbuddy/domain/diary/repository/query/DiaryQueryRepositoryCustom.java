package moodbuddy.moodbuddy.domain.diary.repository.query;

import moodbuddy.moodbuddy.domain.diary.domain.type.DiaryEmotion;
import moodbuddy.moodbuddy.domain.diary.dto.request.query.DiaryReqFilterDTO;
import moodbuddy.moodbuddy.domain.diary.dto.response.query.DiaryResQueryDTO;
import moodbuddy.moodbuddy.global.common.base.PageCustom;
import org.springframework.data.domain.Pageable;

public interface DiaryQueryRepositoryCustom {
    PageCustom<DiaryResQueryDTO> findDiariesWithPageable(Long userId, boolean isAscending, Pageable pageable);
    PageCustom<DiaryResQueryDTO> findDiariesByEmotionWithPageable(Long userId, boolean isAscending, DiaryEmotion emotion, Pageable pageable);
    PageCustom<DiaryResQueryDTO> findDiariesByFilterWithPageable(Long userId, boolean isAscending, DiaryReqFilterDTO filterDTO, Pageable pageable);
}
