package moodbuddy.moodbuddy.domain.diary.facade.query;

import lombok.RequiredArgsConstructor;
import moodbuddy.moodbuddy.domain.diary.domain.type.DiaryEmotion;
import moodbuddy.moodbuddy.domain.diary.dto.request.query.DiaryReqFilterDTO;
import moodbuddy.moodbuddy.domain.diary.dto.response.query.DiaryResQueryDTO;
import moodbuddy.moodbuddy.domain.diary.service.query.DiaryQueryService;
import moodbuddy.moodbuddy.global.common.base.PageCustom;
import moodbuddy.moodbuddy.global.util.JwtUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryQueryFacadeImpl implements DiaryQueryFacade {
    private final DiaryQueryService diaryQueryService;

    @Override
    public PageCustom<DiaryResQueryDTO> getDiaries(Pageable pageable) {
        return diaryQueryService.getDiaries(JwtUtil.getUserId(), getIsAscending(pageable), pageable);
    }

    @Override
    public PageCustom<DiaryResQueryDTO> getDiariesByEmotion(DiaryEmotion diaryEmotion, Pageable pageable) {
        return diaryQueryService.getDiariesByEmotion(JwtUtil.getUserId(), getIsAscending(pageable), diaryEmotion, pageable);
    }

    @Override
    public PageCustom<DiaryResQueryDTO> getDiariesByFilter(DiaryReqFilterDTO requestDTO, Pageable pageable) {
        return diaryQueryService.getDiariesByFilter(JwtUtil.getUserId(), getIsAscending(pageable), requestDTO, pageable);
    }

    private boolean getIsAscending(Pageable pageable) {
        return pageable.getSort().stream()
                .anyMatch(order -> order.getProperty().equals("date") && order.getDirection().isAscending());
    }
}