package moodbuddy.moodbuddy.infra.batch.config;

import lombok.RequiredArgsConstructor;
import moodbuddy.moodbuddy.domain.diary.domain.type.DiaryEmotion;
import moodbuddy.moodbuddy.domain.diary.domain.type.DiarySubject;
import moodbuddy.moodbuddy.domain.diary.service.count.DiaryCountService;
import moodbuddy.moodbuddy.domain.quddyTI.domain.QuddyTI;
import moodbuddy.moodbuddy.global.common.base.type.MoodBuddyStatus;
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
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class QuddyTIUpdateBatchConfig {
    private final DataSource dataSource;
    private final JobRepository jobRepository;
    private final DiaryCountService diaryCountService;
    private final QuddyTIBatchJDBCRepository quddyTIBatchJDBCRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job quddyTIUpdateJob(Step quddyTIUpdateStep) {
        return new JobBuilder("quddyTIUpdateJob", jobRepository)
                .start(quddyTIUpdateStep)
                .build();
    }

    @Bean
    public Step quddyTIUpdateStep() {
        return new StepBuilder("quddyTIUpdateStep", jobRepository)
                .<QuddyTI, QuddyTI>chunk(1000, transactionManager)
                .reader(quddyTIReader())
                .processor(findCountProcessor())
                .writer(updateQuddyTIWriter())
                .build();
    }

    @Bean
    public JdbcCursorItemReader<QuddyTI> quddyTIReader() {
        LocalDate[] dates = DateUtil.getLastMonthDates();
        return new JdbcCursorItemReaderBuilder<QuddyTI>()
                .dataSource(dataSource)
                .name("quddyTIReader")
                .sql(
                """
                SELECT id, user_id, quddy_ti_year, quddy_ti_month, mood_buddy_status
                FROM quddy_ti 
                WHERE quddy_ti_year = ? AND quddy_ti_month = ? AND mood_buddy_status = ?
                """
                )
                .queryArguments(
                        DateUtil.formatYear(dates[0]),
                        DateUtil.formatMonth(dates[1]),
                        MoodBuddyStatus.DIS_ACTIVE.name()
                )
                .rowMapper(this::mapQuddyTI)
                .fetchSize(50)
                .saveState(false)
                .build();
    }

    private QuddyTI mapQuddyTI(ResultSet rs, int rowNum) throws SQLException {
        return QuddyTI.builder()
                .id(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .quddyTIYear(rs.getString("quddy_ti_year"))
                .quddyTIMonth(rs.getString("quddy_ti_month"))
                .moodBuddyStatus(MoodBuddyStatus.valueOf(rs.getString("mood_buddy_status")))
                .build();
    }

    @Bean
    public ItemProcessor<QuddyTI, QuddyTI> findCountProcessor() {
        return quddyTI -> {
            LocalDate[] dates = DateUtil.getLastMonthDates();
            Map<DiaryEmotion, Long> emotionCounts = diaryCountService.getEmotionCountsByDate(quddyTI.getUserId(), dates);
            Map<DiarySubject, Long> subjectCounts = diaryCountService.getSubjectCountsByDate(quddyTI.getUserId(), dates);
            quddyTI.update(emotionCounts, subjectCounts);
            return quddyTI;
        };
    }

    @Bean
    public ItemWriter<QuddyTI> updateQuddyTIWriter() {
        return items -> quddyTIBatchJDBCRepository.bulkUpdate(items.getItems());
    }
}