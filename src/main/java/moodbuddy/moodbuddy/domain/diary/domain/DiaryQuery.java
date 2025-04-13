package moodbuddy.moodbuddy.domain.diary.domain;

import jakarta.persistence.*;
import lombok.*;
import moodbuddy.moodbuddy.domain.diary.domain.type.DiaryEmotion;
import moodbuddy.moodbuddy.domain.diary.domain.type.DiarySubject;
import moodbuddy.moodbuddy.global.common.base.type.MoodBuddyStatus;
import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "diary_query",
        uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "date"}),
        indexes = {
                @Index(name = "idx_user_Id_mood_buddy_status_date", columnList = "user_id, mood_buddy_status, date")
        }
)
public class DiaryQuery {
    @Id
    @Column(name = "diary_id")
    private Long diaryId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "user_id", nullable = false, columnDefinition = "bigint")
    private Long userId;
    
    @Column(name = "thumbnail", columnDefinition = "text")
    private String thumbnail;

    @Enumerated(EnumType.STRING)
    @Column(name = "emotion")
    private DiaryEmotion emotion;

    @Enumerated(EnumType.STRING)
    @Column(name = "subject")
    private DiarySubject subject;

    @Enumerated(EnumType.STRING)
    @Column(name = "mood_buddy_status")
    private MoodBuddyStatus moodBuddyStatus;

    public static DiaryQuery from(Diary diary) {
        String previewContent = diary.getContent();
        if (previewContent.length() > 100) {
            previewContent = previewContent.substring(0, 100) + "..."; }
        return DiaryQuery.builder()
                .diaryId(diary.getId())
                .title(diary.getTitle())
                .date(diary.getDate())
                .content(previewContent)
                .userId(diary.getUserId())
                .thumbnail(diary.getThumbnail())
                .emotion(diary.getEmotion())
                .subject(diary.getSubject())
                .moodBuddyStatus(diary.getMoodBuddyStatus())
                .build();
    }

    public void update(Diary diary) {
        this.title = diary.getTitle();
        this.date = diary.getDate();
        this.content = diary.getContent();
        this.userId = diary.getUserId();
        this.thumbnail = diary.getThumbnail();
        this.emotion = diary.getEmotion();
        this.subject = diary.getSubject();
        this.moodBuddyStatus = diary.getMoodBuddyStatus();
    }
}