package moodbuddy.moodbuddy.domain.letter.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LetterReqDTO {
    @Schema(description = "답장 형식(따뜻한 위로의 말 = 1, 따끔한 해결의 말 = 0)")
    private Integer letterFormat;
    @Schema(description = "사용자가 작성한 고민 편지 내용")
    private String letterWorryContent;
    @Schema(description = "사용자가 작성한 고민 편지 내용에 대한 답장")
    private String letterAnswerContent;
    @Schema(description = "사용자가 고민 편지를 작성한 날짜")
    private LocalDateTime letterDate;
}
