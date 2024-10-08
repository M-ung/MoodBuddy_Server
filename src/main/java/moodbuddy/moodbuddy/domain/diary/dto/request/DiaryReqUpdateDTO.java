package moodbuddy.moodbuddy.domain.diary.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moodbuddy.moodbuddy.domain.diary.domain.DiaryFont;
import moodbuddy.moodbuddy.domain.diary.domain.DiaryFontSize;
import moodbuddy.moodbuddy.domain.diary.domain.DiaryStatus;
import moodbuddy.moodbuddy.domain.diary.domain.DiaryWeather;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiaryReqUpdateDTO {
    @Schema(description = "수정할 일기 고유 식별자(diaryId)", example = "1")
    private Long diaryId;
    @Schema(description = "수정할 일기 제목", example = "쿼카의 하카")
    private String diaryTitle;
    @Schema(description = "수정할 일기 날짜", example = "2023-07-02T15:30:00")
    private LocalDate diaryDate;
    @Schema(description = "수정할 일기 내용", example = "쿼카쿼카쿼카쿼카쿼카쿼카")
    private String diaryContent;
    @Schema(description = "수정할 일기 날씨(CLEAR, CLOUDY, RAIN, SNOW)", example = "CLEAR")
    private DiaryWeather diaryWeather;
    @Schema(description = "수정할 일기 상태(DRAFT, PUBLISHED)", example = "DRAFT")
    private DiaryStatus diaryStatus;
    @Schema(description = "수정할 일기 이미지 List", example = "[\"image1.png\", \"image2.png\"]")
    private List<MultipartFile> diaryImgList;
    @Schema(description = "유지할 일기 이미지 List", example = "[\"이미지 URL\", \"이미지 URL\"]")
    private List<String> existingDiaryImgList;
    @Schema(description = "일기 폰트", example = "INTER")
    private DiaryFont diaryFont;
    @Schema(description = "일기 폰트 사이즈", example = "PX30")
    private DiaryFontSize diaryFontSize;
}
