package moodbuddy.moodbuddy.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserReqMonthCommentUpdateDTO {
    @Schema(description = "월별 통계에서 선택한 달", example = "2024-06")
    private String chooseMonth;
    @Schema(description = "다음 달 나에게 짧은 한 마디 수정 내용")
    private String monthComment;
}
