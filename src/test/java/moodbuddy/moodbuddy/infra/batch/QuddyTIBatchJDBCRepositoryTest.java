package moodbuddy.moodbuddy.infra.batch;

import moodbuddy.moodbuddy.domain.diary.domain.Diary;
import moodbuddy.moodbuddy.domain.diary.domain.type.DiaryEmotion;
import moodbuddy.moodbuddy.domain.diary.domain.type.DiarySubject;
import moodbuddy.moodbuddy.domain.diary.domain.type.DiaryWeather;
import moodbuddy.moodbuddy.domain.diary.repository.DiaryRepository;
import moodbuddy.moodbuddy.domain.quddyTI.domain.QuddyTI;
import moodbuddy.moodbuddy.domain.quddyTI.repository.QuddyTIRepository;
import moodbuddy.moodbuddy.global.common.base.type.DiaryFont;
import moodbuddy.moodbuddy.global.common.base.type.DiaryFontSize;
import moodbuddy.moodbuddy.global.common.base.type.MoodBuddyStatus;
import moodbuddy.moodbuddy.infra.batch.repository.QuddyTIBatchJDBCRepository;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QuddyTIBatchJDBCRepositoryTest {
    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private QuddyTIRepository quddyTIRepository;
    @Autowired
    private QuddyTIBatchJDBCRepository quddyTIBatchJDBCRepository;

    @BeforeAll
    @DisplayName("setUp")
    public void setUp() {
        for (int i = 0; i < 10; i++) {
            diaryRepository.save(Diary.builder()
                    .userId(2L)
                    .date(LocalDate.now().minusDays(i))
                    .title("Diary " + i)
                    .content("Content of Diary " + i)
                    .weather(DiaryWeather.SNOW)
                    .emotion(DiaryEmotion.HAPPINESS)
                    .subject(DiarySubject.DAILY)
                    .font(DiaryFont.INTER)
                    .fontSize(DiaryFontSize.PX30)
                    .moodBuddyStatus(MoodBuddyStatus.ACTIVE)
                    .build());
        }
        quddyTIRepository.save(QuddyTI.builder()
                .userId(2L)
                .quddyTIYear("2025")
                .quddyTIMonth("01")
                .diaryFrequency(2)
                .dailyCount(1)
                .growthCount(1)
                .emotionCount(2)
                .travelCount(0)
                .happinessCount(1)
                .angerCount(0)
                .disgustCount(0)
                .fearCount(0)
                .neutralCount(0)
                .sadnessCount(1)
                .surpriseCount(0)
                .quddyTIType("TEST")
                .moodBuddyStatus(MoodBuddyStatus.ACTIVE)
                .build());
    }

    @AfterAll
    @DisplayName("finish")
    public void finish() {
        diaryRepository.deleteAll();
        quddyTIRepository.deleteAll();
    }

//    @Test
//    @DisplayName("QuddyTI 찾기 테스트")
//    public void QuddyTI_찾기_테스트() {
//        QuddyTI quddyTI = quddyTIBatchJDBCRepository.findQuddyTIByUserIdAndDate(2L, "2025", "01").get();
//        assertNotNull(quddyTI);
//        assertEquals(2L, quddyTI.getUserId());
//        assertEquals("2025", quddyTI.getQuddyTIYear());
//        assertEquals("01", quddyTI.getQuddyTIMonth());
//        assertEquals("TEST", quddyTI.getQuddyTIType());
//    }

    @Test
    @DisplayName("한 달 기준 일기 감정 갯수 세기 테스트")
    public void 한_달_기준_일기_감정_갯수_세기_테스트() {
        LocalDate start = LocalDate.of(2024, 2, 1);
        LocalDate end = LocalDate.of(2026, 2, 28);
        long happinessCount = quddyTIBatchJDBCRepository.findEmotionCountsByUserIdAndDate(2L, DiaryEmotion.HAPPINESS, start, end);
        assertEquals(10L, happinessCount);
        long sadnessCount = quddyTIBatchJDBCRepository.findEmotionCountsByUserIdAndDate(2L, DiaryEmotion.SADNESS, start, end);
        assertEquals(0L, sadnessCount);

    }

    @Test
    @DisplayName("한 달 기준 일기 주제 갯수 세기 테스트")
    public void 한_달_기준_일기_주제_갯수_세기_테스트() {
        LocalDate start = LocalDate.of(2024, 2, 1);
        LocalDate end = LocalDate.of(2026, 2, 28);
        long dailyCount = quddyTIBatchJDBCRepository.findSubjectCountsByUserIdAndDate(2L, DiarySubject.DAILY, start, end);
        assertEquals(10L, dailyCount);
        long travelCount = quddyTIBatchJDBCRepository.findSubjectCountsByUserIdAndDate(2L, DiarySubject.TRAVEL, start, end);
        assertEquals(0L, travelCount);
    }
}