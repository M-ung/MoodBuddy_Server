package moodbuddy.moodbuddy.domain.draftDiary.service;

import moodbuddy.moodbuddy.domain.diary.domain.type.DiaryWeather;
import moodbuddy.moodbuddy.domain.draftDiary.domain.DraftDiary;
import moodbuddy.moodbuddy.domain.draftDiary.dto.request.DraftDiaryReqPublishDTO;
import moodbuddy.moodbuddy.domain.draftDiary.dto.request.DraftDiaryReqSelectDeleteDTO;
import moodbuddy.moodbuddy.domain.draftDiary.repository.DraftDiaryRepository;
import moodbuddy.moodbuddy.global.common.base.type.DiaryFont;
import moodbuddy.moodbuddy.global.common.base.type.DiaryFontSize;
import moodbuddy.moodbuddy.global.common.base.type.MoodBuddyStatus;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DraftDiaryCurrentTest {
    private static final int THREAD_COUNT = 5;
    @Autowired
    private DraftDiaryService draftDiaryService;
    @Autowired
    private DraftDiaryRepository draftDiaryRepository;
    private List<DraftDiary> draftDiaries;
    private Long draftDiaryId;
    private DraftDiaryReqPublishDTO requestDTO;
    private DraftDiaryReqSelectDeleteDTO selectDeleteDTO;

    @BeforeEach
    @DisplayName("setUp")
    public void setUp() {
        draftDiaries = createTestDraftDiaries();
        draftDiaryId = draftDiaries.getFirst().getId();
        requestDTO = createDraftDiaryReqPublishDTO(draftDiaryId);
        selectDeleteDTO = createDraftDiaryReqSelectDeleteDTO(draftDiaries);
    }

    @AfterEach
    @DisplayName("finish")
    public void finish() {
        draftDiaryRepository.deleteAll();
    }

    @Test
    @DisplayName("임시저장 일기 출판할 때 동시성 테스트")
    public void 임시저장_일기_출판할_때_동시성_테스트() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    draftDiaryService.publish(1L, requestDTO);
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

        Optional<DraftDiary> findDraftDiary = draftDiaryRepository.findById(draftDiaryId);
        assertThat(findDraftDiary.get().getMoodBuddyStatus()).isEqualTo(MoodBuddyStatus.DIS_ACTIVE);
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(THREAD_COUNT - 1);
    }

    @Test
    @DisplayName("임시저장 삭제할 때 동시성 테스트")
    public void 임시저장_삭제할_때_동시성_테스트 () throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    draftDiaryService.delete(1L, selectDeleteDTO);
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

        for(int i=0; i<selectDeleteDTO.diaryIdList().size(); i++) {
            Long draftDiaryId = selectDeleteDTO.diaryIdList().get(i);
            Optional<DraftDiary> findDraftDiary = draftDiaryRepository.findById(draftDiaryId);
            assertThat(findDraftDiary.get().getMoodBuddyStatus()).isEqualTo(MoodBuddyStatus.DIS_ACTIVE);
        }
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(THREAD_COUNT - 1);
    }

    private List<DraftDiary> createTestDraftDiaries() {
        List<DraftDiary> draftDiaries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            DraftDiary draftDiary = DraftDiary.builder()
                    .title("임시저장 제목")
                    .date(LocalDate.of(2023, 7, 2))
                    .content("임시저장 내용")
                    .weather(DiaryWeather.CLEAR)
                    .userId(1L)
                    .font(DiaryFont.INTER)
                    .fontSize(DiaryFontSize.PX30)
                    .moodBuddyStatus(MoodBuddyStatus.ACTIVE)
                    .build();
            draftDiaryRepository.save(draftDiary);
            draftDiaries.add(draftDiary);
        }
        return draftDiaries;
    }

    private DraftDiaryReqPublishDTO createDraftDiaryReqPublishDTO(Long diaryId) {
        return new DraftDiaryReqPublishDTO(
                diaryId,
                "출판된 제목",
                LocalDate.now(),
                "출판된 내용",
                DiaryWeather.CLOUDY,
                DiaryFont.INTER,
                DiaryFontSize.PX24,
                List.of("imageUrl3", "imageUrl4")
        );
    }

    private DraftDiaryReqSelectDeleteDTO createDraftDiaryReqSelectDeleteDTO(List<DraftDiary> draftDiaries) {
        List<Long> diaryIdList = new ArrayList<>();
        for (DraftDiary draftDiary : draftDiaries) {
            diaryIdList.add(draftDiary.getId());
        }
        return new DraftDiaryReqSelectDeleteDTO(diaryIdList);
    }
}