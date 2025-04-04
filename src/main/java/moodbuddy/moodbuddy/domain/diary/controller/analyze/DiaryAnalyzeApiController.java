package moodbuddy.moodbuddy.domain.diary.controller.analyze;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import moodbuddy.moodbuddy.domain.diary.dto.response.emotion.DiaryResAnalyzeDTO;
import moodbuddy.moodbuddy.domain.diary.facade.analyze.DiaryAnalyzeFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/member/diary")
@Tag(name = "Diary", description = "일기 관련 API")
@RequiredArgsConstructor
public class DiaryAnalyzeApiController {
    private final DiaryAnalyzeFacade diaryAnalyzeFacade;

    @PostMapping("/analyze/{diaryId}")
    @Operation(summary = "일기 분석 (요약, 주제, 감정)", description = "작성된 일기를 분석합니다. 일기 저장, 일기 수정, 임시저장 일기 -> 일기 저장 할 때 호출하면 됩니다.")
    public ResponseEntity<DiaryResAnalyzeDTO> analyze(@Parameter(description = "일기 고유 식별자")
                                                           @PathVariable("diaryId") Long diaryId) {
        return ResponseEntity.ok().body(diaryAnalyzeFacade.analyze(diaryId));
    }
}
