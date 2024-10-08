package moodbuddy.moodbuddy.domain.quddyTI.scheduler;

import lombok.RequiredArgsConstructor;
import moodbuddy.moodbuddy.domain.quddyTI.service.QuddyTIService;
import moodbuddy.moodbuddy.domain.quddyTI.service.QuddyTIServiceImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class QuddyTIScheduler {
    private final QuddyTIService quddyTIService;
    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void aggregateAndSaveDiaryData() {
        quddyTIService.aggregateAndSaveDiaryData();
    }
}
