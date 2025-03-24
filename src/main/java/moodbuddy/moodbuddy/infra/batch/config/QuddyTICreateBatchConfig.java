package moodbuddy.moodbuddy.infra.batch.config;

import lombok.RequiredArgsConstructor;
import moodbuddy.moodbuddy.domain.quddyTI.domain.QuddyTI;
import moodbuddy.moodbuddy.global.util.DateUtil;
import moodbuddy.moodbuddy.infra.batch.repository.QuddyTIBatchJDBCRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;
import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
@EnableTransactionManagement
public class QuddyTICreateBatchConfig {

    private final JobRepository jobRepository;
    private final DataSource dataSource;
    private final QuddyTIBatchJDBCRepository quddyTIBatchJDBCRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job quddyTICreateJob(Step quddyTICreateStep) {
        return new JobBuilder("quddyTICreateJob", jobRepository)
                .start(quddyTICreateStep)
                .build();
    }

    @Bean
    public Step quddyTICreateStep() {
        return new StepBuilder("quddyTICreateStep", jobRepository)
                .<Long, QuddyTI>chunk(100, transactionManager)
                .reader(userIdReader())
                .processor(createQuddyTIProcessor())
                .writer(saveQuddyTIWriter())
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Long> userIdReader() {
        return new JdbcCursorItemReaderBuilder<Long>()
                .dataSource(dataSource)
                .name("userIdReader")
                .sql("SELECT id FROM user WHERE deleted = 0")
                .rowMapper((rs, rowNum) -> rs.getLong("id"))
                .fetchSize(100)
                .saveState(false)
                .build();
    }

    @Bean
    public ItemProcessor<Long, QuddyTI> createQuddyTIProcessor() {
        return userId -> QuddyTI.of(
                userId,
                DateUtil.formatYear(LocalDate.now()),
                DateUtil.formatMonth(LocalDate.now())
        );
    }

    @Bean
    public ItemWriter<QuddyTI> saveQuddyTIWriter() {
        return items -> quddyTIBatchJDBCRepository.bulkSave(items.getItems());
    }
}