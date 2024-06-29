package moodbuddy.moodbuddy.domain.diary.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moodbuddy.moodbuddy.domain.diary.entity.DiaryEmotion;
import moodbuddy.moodbuddy.domain.diary.entity.DiaryStatus;
import moodbuddy.moodbuddy.domain.diary.entity.DiaryWeather;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiaryResFindOneDTO {
    private Long diaryId;
    private String diaryTitle;
    private LocalDateTime diaryDate;
    private String diaryContent;
    private DiaryWeather diaryWeather;
    private DiaryEmotion diaryEmotion;
    private DiaryStatus diaryStatus;
    private List<String> diaryImgList;
    private String userEmail;
}
