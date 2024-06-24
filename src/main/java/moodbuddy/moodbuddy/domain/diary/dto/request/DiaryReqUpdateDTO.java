package moodbuddy.moodbuddy.domain.diary.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import moodbuddy.moodbuddy.domain.diary.entity.DiaryWeather;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DiaryReqUpdateDTO {
    @NotNull
    private Long diaryId;

    private String diaryTitle;
    private LocalDateTime diaryDate;
    private String diaryContent;
    private DiaryWeather diaryWeather;
    private List<MultipartFile> diaryImgList;
    private List<String> imagesToDelete;
}
