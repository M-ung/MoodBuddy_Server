package moodbuddy.moodbuddy.domain.diary.domain.image;

import jakarta.persistence.*;
import lombok.*;
import moodbuddy.moodbuddy.global.common.base.BaseTimeEntity;
import moodbuddy.moodbuddy.global.common.base.type.MoodBuddyStatus;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(
        name = "diary_image",
        indexes = {
                @Index(name = "idx_diary_id_mood_buddy_status", columnList = "diary_id, mood_buddy_status")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiaryImage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "diary_id")
    private Long diaryId;

    @Column(name = "image_url", columnDefinition = "text")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "mood_buddy_status")
    private MoodBuddyStatus moodBuddyStatus;

    public static DiaryImage of(Long diaryId, String imageUrl) {
        return DiaryImage.builder()
                .diaryId(diaryId)
                .imageUrl(imageUrl)
                .moodBuddyStatus(MoodBuddyStatus.ACTIVE)
                .build();
    }

    public void updateMoodBuddyStatus(MoodBuddyStatus moodBuddyStatus) { this.moodBuddyStatus = moodBuddyStatus; }
}