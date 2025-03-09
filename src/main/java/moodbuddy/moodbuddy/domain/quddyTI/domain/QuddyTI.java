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
import java.util.Optional;

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
        this.happinessCount = emotionCounts.getOrDefault(DiaryEmotion.HAPPINESS, 0L).intValue();
        this.angerCount = emotionCounts.getOrDefault(DiaryEmotion.ANGER, 0L).intValue();
        this.disgustCount = emotionCounts.getOrDefault(DiaryEmotion.DISGUST, 0L).intValue();
        this.fearCount = emotionCounts.getOrDefault(DiaryEmotion.FEAR, 0L).intValue();
        this.neutralCount = emotionCounts.getOrDefault(DiaryEmotion.NEUTRAL, 0L).intValue();
        this.sadnessCount = emotionCounts.getOrDefault(DiaryEmotion.SADNESS, 0L).intValue();
        this.surpriseCount = emotionCounts.getOrDefault(DiaryEmotion.SURPRISE, 0L).intValue();
        this.dailyCount = subjectCounts.getOrDefault(DiarySubject.DAILY, 0L).intValue();
        this.growthCount = subjectCounts.getOrDefault(DiarySubject.GROWTH, 0L).intValue();
        this.emotionCount = subjectCounts.getOrDefault(DiarySubject.EMOTION, 0L).intValue();
        this.travelCount = subjectCounts.getOrDefault(DiarySubject.TRAVEL, 0L).intValue();
        this.quddyTIType = generateType(emotionCounts, subjectCounts);
        this.moodBuddyStatus = MoodBuddyStatus.ACTIVE;
        this.diaryFrequency = (int) emotionCounts.values().stream().mapToLong(Long::longValue).sum();
    }

    private String generateType(Map<DiaryEmotion, Long> emotionCounts, Map<DiarySubject, Long> subjectCounts) {
        String diaryType = getDiaryFrequent(emotionCounts);
        if (QuddyTIType.NO_DIARY.getValue().equals(diaryType)) {
            return QuddyTIType.NO_DIARY.getValue();
        }
        String mostFrequentSubject = getMostFrequentEntry(subjectCounts)
                .map(subject -> subject.name().substring(0, 1))
                .orElseThrow(() -> new QuddyTIInvalidateException(ErrorCode.QUDDYTI_INVALIDATE));
        String mostFrequentEmotion = getMostFrequentEntry(emotionCounts)
                .map(this::getEmotionAbbreviation)
                .orElseThrow(() -> new QuddyTIInvalidateException(ErrorCode.QUDDYTI_INVALIDATE));

        return diaryType + mostFrequentSubject + mostFrequentEmotion;
    }

    private String getDiaryFrequent(Map<DiaryEmotion, Long> emotionCounts) {
        long totalDiaryCount = emotionCounts.values().stream().mapToLong(Long::longValue).sum();
        return totalDiaryCount == 0 ? QuddyTIType.NO_DIARY.getValue() : (totalDiaryCount >= 15 ? QuddyTIType.TYPE_J.getValue() : QuddyTIType.TYPE_P.getValue());
    }

    private <T> Optional<T> getMostFrequentEntry(Map<T, Long> counts) {
        return counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    private String getEmotionAbbreviation(DiaryEmotion emotion) {
        return Optional.ofNullable(emotion)
                .map(DiaryEmotion::getAbbreviation)
                .orElse(QuddyTIType.NO_DIARY.getValue());
    }
}