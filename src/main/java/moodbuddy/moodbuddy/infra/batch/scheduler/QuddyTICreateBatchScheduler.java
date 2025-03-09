package moodbuddy.moodbuddy.infra.batch.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuddyTICreateBatchScheduler {
    private final JobLauncher jobLauncher;
    private final Job quddyTICreateJob;

    @Scheduled(cron = "0 58 23 L * ?")
    public void runBatchJob() throws JobExecutionException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(quddyTICreateJob, jobParameters);
    }
}