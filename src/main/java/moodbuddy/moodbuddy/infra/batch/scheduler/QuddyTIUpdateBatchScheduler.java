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
public class QuddyTIUpdateBatchScheduler {
    private final JobLauncher jobLauncher;
    private final Job quddyTIUpdateJob;

    @Scheduled(cron = "0 0 0 1 * ?")
    public void runBatchJob() throws JobExecutionException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(quddyTIUpdateJob, jobParameters);
    }
}