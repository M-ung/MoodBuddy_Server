package moodbuddy.moodbuddy.infra.batch.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class QuddyTIUpdateBatchScheduler {
    private final JobLauncher jobLauncher;
    private final Job quddyTIUpdateJob;

    @Scheduled(cron = "0 0/2 * * * ?")
    public void runBatchJob() throws JobExecutionException {
        long start = System.currentTimeMillis();
        System.out.println("QuddyTI Update Batch 시작: " + LocalDateTime.now());

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(quddyTIUpdateJob, jobParameters);

        long end = System.currentTimeMillis();
        System.out.println("QuddyTI Update Batch 종료: " + LocalDateTime.now());
        System.out.println("총 실행 시간(ms): " + (end - start));
    }
}