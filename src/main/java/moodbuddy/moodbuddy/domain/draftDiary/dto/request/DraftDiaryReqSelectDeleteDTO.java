package moodbuddy.moodbuddy.domain.draftDiary.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record DraftDiaryReqSelectDeleteDTO(
        @Schema(description = "삭제하고 싶은 임시 저장 일기 고유 식별자(draftDiaryId)를 담는 List", example = "[1, 2]")
        @NotNull
        List<Long> diaryIdList) {
}
