package moodbuddy.moodbuddy.domain.quddyTI.domain;

import jakarta.persistence.*;
import lombok.*;
import moodbuddy.moodbuddy.domain.diary.domain.type.DiaryEmotion;
import moodbuddy.moodbuddy.domain.diary.domain.type.DiarySubject;
import moodbuddy.moodbuddy.domain.quddyTI.domain.type.QuddyTIType;
import moodbuddy.moodbuddy.domain.quddyTI.exception.QuddyTIInvalidateException;
import moodbuddy.moodbuddy.global.common.base.BaseTimeEntity;
import moodbuddy.moodbuddy.global.common.base.type.MoodBuddyStatus;
import moodbuddy.moodbuddy.global.error.ErrorCode;
import java.util.Map;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "quddy_ti", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "quddy_ti_year", "quddy_ti_month"})
})
public class QuddyTI extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false, columnDefinition = "bigint")
    private Long userId;
    @Column(name = "quddy_ti_year", columnDefinition = "varchar(4)")
    private String quddyTIYear;
    @Column(name = "quddy_ti_month", columnDefinition = "varchar(2)")
    private String quddyTIMonth;

    @Column(name = "diary_frequency", columnDefinition = "int")
    private Integer diaryFrequency;
    @Column(name = "daily_count", columnDefinition = "int")
    private Integer dailyCount;
    @Column(name = "growth_count", columnDefinition = "int")
    private Integer growthCount;
    @Column(name = "emotion_count", columnDefinition = "int")
    private Integer emotionCount;
    @Column(name = "travel_count", columnDefinition = "int")
    private Integer travelCount;

    /** 감정 **/
    @Column(name = "happiness_count", columnDefinition = "int")
    private Integer happinessCount;
    @Column(name = "anger_count", columnDefinition = "int")
    private Integer angerCount;
    @Column(name = "disgust_count", columnDefinition = "int")
    private Integer disgustCount;
    @Column(name = "fear_count", columnDefinition = "int")
    private Integer fearCount;
    @Column(name = "neutral_count", columnDefinition = "int")
    private Integer neutralCount;
    @Column(name = "sadness_count", columnDefinition = "int")
    private Integer sadnessCount;
    @Column(name = "surprise_count", columnDefinition = "int")
    private Integer surpriseCount;

    /** 쿼디티아이 **/
    @Column(name = "quddy_ti_type", columnDefinition = "varchar(10)")
    private String quddyTIType;

    /** 쿼디티아이 상태 **/
    @Enumerated(EnumType.STRING)
    @Column(name = "mood_buddy_status")
    private MoodBuddyStatus moodBuddyStatus;

    public static QuddyTI of(Long userId, String year, String month) {
        return QuddyTI.builder()
                .userId(userId)
                .quddyTIYear(year)
                .quddyTIMonth(month)
                .diaryFrequency(0)
                .dailyCount(0)
                .growthCount(0)
                .emotionCount(0)
                .travelCount(0)
                .happinessCount(0)
                .angerCount(0)
                .disgustCount(0)
                .fearCount(0)
                .neutralCount(0)
                .sadnessCount(0)
                .surpriseCount(0)
                .quddyTIType(null)
                .moodBuddyStatus(MoodBuddyStatus.DIS_ACTIVE)
                .build();
    }

    public void update(Map<DiaryEmotion, Long> emotionCounts, Map<DiarySubject, Long> subjectCounts) {
        this.happinessCount = getCount(emotionCounts, DiaryEmotion.HAPPINESS);
        this.angerCount = getCount(emotionCounts, DiaryEmotion.ANGER);
        this.disgustCount = getCount(emotionCounts, DiaryEmotion.DISGUST);
        this.fearCount = getCount(emotionCounts, DiaryEmotion.FEAR);
        this.neutralCount = getCount(emotionCounts, DiaryEmotion.NEUTRAL);
        this.sadnessCount = getCount(emotionCounts, DiaryEmotion.SADNESS);
        this.surpriseCount = getCount(emotionCounts, DiaryEmotion.SURPRISE);
        this.dailyCount = getCount(subjectCounts, DiarySubject.DAILY);
        this.growthCount = getCount(subjectCounts, DiarySubject.GROWTH);
        this.emotionCount = getCount(subjectCounts, DiarySubject.EMOTION);
        this.travelCount = getCount(subjectCounts, DiarySubject.TRAVEL);
        this.diaryFrequency = emotionCounts.values().stream().mapToInt(Long::intValue).sum();
        this.quddyTIType = generateType(emotionCounts, subjectCounts);
        this.moodBuddyStatus = MoodBuddyStatus.ACTIVE;
    }

    private <T> int getCount(Map<T, Long> map, T key) {
        return map.getOrDefault(key, 0L).intValue();
    }

    private String generateType(Map<DiaryEmotion, Long> emotions, Map<DiarySubject, Long> subjects) {
        int total = emotions.values().stream().mapToInt(Long::intValue).sum();
        if (total == 0) return QuddyTIType.NO_DIARY.getValue();

        String type = total >= 15 ? QuddyTIType.TYPE_J.getValue() : QuddyTIType.TYPE_P.getValue();
        String subject = subjects.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow(() -> new QuddyTIInvalidateException(ErrorCode.QUDDYTI_INVALIDATE))
                .getKey().name().substring(0, 1);
        String emotion = emotions.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow(() -> new QuddyTIInvalidateException(ErrorCode.QUDDYTI_INVALIDATE))
                .getKey().name().substring(0, 1);

        return type + subject + emotion;
    }
}