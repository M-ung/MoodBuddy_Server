package moodbuddy.moodbuddy.domain.diary.service;

import moodbuddy.moodbuddy.domain.diary.domain.Diary;
import moodbuddy.moodbuddy.domain.diary.domain.type.DiaryWeather;
import moodbuddy.moodbuddy.domain.diary.dto.request.save.DiaryReqSaveDTO;
import moodbuddy.moodbuddy.domain.diary.dto.request.update.DiaryReqUpdateDTO;
import moodbuddy.moodbuddy.domain.diary.repository.DiaryRepository;
import moodbuddy.moodbuddy.global.common.base.type.DiaryFont;
import moodbuddy.moodbuddy.global.common.base.type.DiaryFontSize;
import moodbuddy.moodbuddy.global.common.base.type.MoodBuddyStatus;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DiaryCurrentTest {
    private static final int THREAD_COUNT = 10;
    @Autowired
    private DiaryService diaryService;
    @Autowired
    private DiaryRepository diaryRepository;
    private Diary diary;
    private Long diaryId;
    private DiaryReqSaveDTO saveRequestDTO;
    private DiaryReqUpdateDTO updateRequestDTO;

    @BeforeAll
    @DisplayName("setUp")
    public void setUp() {
        diary = createTestDiary();
        diaryRepository.save(diary);
        diaryId = diary.getId();
        saveRequestDTO = createSaveDTO();
        updateRequestDTO = createUpdateDTO();
    }

    @AfterAll
    @DisplayName("finish")
    public void finish() {
        diaryRepository.deleteAll();
    }

    @Test
    @DisplayName("일기 저장할 때 동시성 테스트")
    public void 일기_저장할_때_동시성_테스트() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    diaryService.save(1L, saveRequestDTO);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    e.printStackTrace();
                } finally{
                    latch.countDown();
                }
            });
        }
        executorService.shutdown();
        latch.await();
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(THREAD_COUNT - 1);
    }

    @Test
    @DisplayName("일기 수정할 때 동시성 테스트")
    public void 일기_수정할_때_동시성_테스트() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    diaryService.update(1L, updateRequestDTO);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    e.printStackTrace();
                } finally{
                    latch.countDown();
                }
            });
        }
        executorService.shutdown();
        latch.await();

        Optional<Diary> findDiary = diaryRepository.findById(diaryId);
        assertThat(findDiary.get().getTitle()).isEqualTo(updateRequestDTO.diaryTitle());
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(THREAD_COUNT - 1);
    }

    @Test
    @DisplayName("일기 삭제할 때 동시성 테스트")
    public void 일기_삭제할_때_동시성_테스트 () throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    diaryService.delete(1L, diaryId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    e.printStackTrace();
                } finally{
                    latch.countDown();
                }
            });
        }
        executorService.shutdown();
        latch.await();
        Optional<Diary> findDiary = diaryRepository.findById(diaryId);
        assertThat(findDiary.get().getMoodBuddyStatus()).isEqualTo(MoodBuddyStatus.DIS_ACTIVE);
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(THREAD_COUNT - 1);
    }

    private Diary createTestDiary() {
        return Diary.builder()
                .title("테스트 일기")
                .date(LocalDate.of(2023, 7, 2))
                .content("일기 내용입니다.")
                .weather(DiaryWeather.CLEAR)
                .userId(1L)
                .font(DiaryFont.INTER)
                .fontSize(DiaryFontSize.PX30)
                .moodBuddyStatus(MoodBuddyStatus.ACTIVE)
                .build();
    }

    private DiaryReqSaveDTO createSaveDTO() {
        return new DiaryReqSaveDTO(
                "쿼카의 하루",
                LocalDate.of(2023, 7, 3),
                "쿼카쿼카쿼카쿼카쿼카쿼카",
                DiaryWeather.CLEAR,
                DiaryFont.INTER,
                DiaryFontSize.PX30,
                List.of("imageUrl1", "imageUrl2")
        );
    }

    private DiaryReqUpdateDTO createUpdateDTO() {
        return new DiaryReqUpdateDTO(
                diaryId,
                "수정된 제목",
                LocalDate.now(),
                "수정된 내용",
                DiaryWeather.CLOUDY,
                DiaryFont.INTER,
                DiaryFontSize.PX24,
                List.of("imageUrl3", "imageUrl4")
        );
    }
}