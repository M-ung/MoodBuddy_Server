package moodbuddy.moodbuddy.infra.batch.repository;

import lombok.RequiredArgsConstructor;
import moodbuddy.moodbuddy.domain.diary.domain.type.DiaryEmotion;
import moodbuddy.moodbuddy.domain.diary.domain.type.DiarySubject;
import moodbuddy.moodbuddy.domain.quddyTI.domain.QuddyTI;
import org.springframework.batch.item.Chunk;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class QuddyTIBatchJDBCRepository {
    private final JdbcTemplate jdbcTemplate;

    public Map<DiaryEmotion, Long> findEmotionGroupCountsByUserIdAndDate(Long userId, LocalDate start, LocalDate end) {
        String sql = "SELECT emotion, COUNT(*) FROM diary WHERE user_id = ? AND date BETWEEN ? AND ? GROUP BY emotion";

        return jdbcTemplate.query(sql, rs -> {
            EnumMap<DiaryEmotion, Long> map = new EnumMap<>(DiaryEmotion.class);
            while (rs.next()) {
                String emotionStr = rs.getString("emotion");
                long count = rs.getLong("COUNT(*)");
                map.put(DiaryEmotion.valueOf(emotionStr), count);
            }
            for (DiaryEmotion e : DiaryEmotion.values()) {
                map.putIfAbsent(e, 0L);
            }
            return map;
        }, userId, start, end);
    }

    public Map<DiarySubject, Long> findSubjectGroupCountsByUserIdAndDate(Long userId, LocalDate start, LocalDate end) {
        String sql = "SELECT subject, COUNT(*) FROM diary WHERE user_id = ? AND date BETWEEN ? AND ? GROUP BY subject";

        return jdbcTemplate.query(sql, rs -> {
            EnumMap<DiarySubject, Long> map = new EnumMap<>(DiarySubject.class);
            while (rs.next()) {
                String subjectStr = rs.getString("subject");
                long count = rs.getLong("COUNT(*)");
                map.put(DiarySubject.valueOf(subjectStr), count);
            }
            for (DiarySubject s : DiarySubject.values()) {
                map.putIfAbsent(s, 0L);
            }
            return map;
        }, userId, start, end);
    }

    public void bulkUpdate(List<? extends QuddyTI> items) {
        if (items == null || items.isEmpty()) return;

        String sql = """
        UPDATE quddy_ti SET 
            diary_frequency = ?, daily_count = ?, growth_count = ?, emotion_count = ?, travel_count = ?, 
            happiness_count = ?, anger_count = ?, disgust_count = ?, fear_count = ?, neutral_count = ?, 
            sadness_count = ?, surprise_count = ?, quddy_ti_type = ?, mood_buddy_status = ?, updated_time = NOW()
        WHERE id = ?
        """;

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                QuddyTI q = items.get(i);
                ps.setInt(1, q.getDiaryFrequency());
                ps.setInt(2, q.getDailyCount());
                ps.setInt(3, q.getGrowthCount());
                ps.setInt(4, q.getEmotionCount());
                ps.setInt(5, q.getTravelCount());
                ps.setInt(6, q.getHappinessCount());
                ps.setInt(7, q.getAngerCount());
                ps.setInt(8, q.getDisgustCount());
                ps.setInt(9, q.getFearCount());
                ps.setInt(10, q.getNeutralCount());
                ps.setInt(11, q.getSadnessCount());
                ps.setInt(12, q.getSurpriseCount());
                ps.setString(13, q.getQuddyTIType());
                ps.setString(14, q.getMoodBuddyStatus().toString());
                ps.setLong(15, q.getId());
            }

            @Override
            public int getBatchSize() {
                return items.size();
            }
        });
    }
    public void bulkSave(List<? extends QuddyTI> items) {
        if (items == null || items.isEmpty()) return;

        String sql = """
            INSERT INTO quddy_ti (
                user_id, quddy_ti_year, quddy_ti_month, diary_frequency, daily_count, growth_count,
                emotion_count, travel_count, happiness_count, anger_count, disgust_count, fear_count,
                neutral_count, sadness_count, surprise_count, quddy_ti_type, mood_buddy_status, created_time, updated_time
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                QuddyTI quddyTI = items.get(i);
                ps.setLong(1, quddyTI.getUserId());
                ps.setString(2, quddyTI.getQuddyTIYear());
                ps.setString(3, quddyTI.getQuddyTIMonth());
                ps.setInt(4, quddyTI.getDiaryFrequency());
                ps.setInt(5, quddyTI.getDailyCount());
                ps.setInt(6, quddyTI.getGrowthCount());
                ps.setInt(7, quddyTI.getEmotionCount());
                ps.setInt(8, quddyTI.getTravelCount());
                ps.setInt(9, quddyTI.getHappinessCount());
                ps.setInt(10, quddyTI.getAngerCount());
                ps.setInt(11, quddyTI.getDisgustCount());
                ps.setInt(12, quddyTI.getFearCount());
                ps.setInt(13, quddyTI.getNeutralCount());
                ps.setInt(14, quddyTI.getSadnessCount());
                ps.setInt(15, quddyTI.getSurpriseCount());
                ps.setString(16, quddyTI.getQuddyTIType());
                ps.setString(17, quddyTI.getMoodBuddyStatus().toString());
                ps.setString(18, LocalDateTime.now().toString());
                ps.setString(19, LocalDateTime.now().toString());
            }

            @Override
            public int getBatchSize() {
                return items.size();
            }
        });
    }
}