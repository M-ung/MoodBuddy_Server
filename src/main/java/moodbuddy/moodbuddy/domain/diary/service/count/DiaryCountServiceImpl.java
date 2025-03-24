package moodbuddy.moodbuddy.domain.diary.service.count;

import lombok.RequiredArgsConstructor;
import moodbuddy.moodbuddy.domain.diary.domain.type.DiaryEmotion;
import moodbuddy.moodbuddy.domain.diary.domain.type.DiarySubject;
import moodbuddy.moodbuddy.infra.batch.repository.QuddyTIBatchJDBCRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryCountServiceImpl implements DiaryCountService {
    private final QuddyTIBatchJDBCRepository quddyTIBatchJDBCRepository;

    @Override
    public Map<DiaryEmotion, Long> getEmotionCountsByDate(Long userId, LocalDate[] dates) {
        return quddyTIBatchJDBCRepository.findEmotionGroupCountsByUserIdAndDate(userId, dates[0], dates[1]);
    }

    @Override
    public Map<DiarySubject, Long> getSubjectCountsByDate(Long userId, LocalDate[] dates) {
        return quddyTIBatchJDBCRepository.findSubjectGroupCountsByUserIdAndDate(userId, dates[0], dates[1]);
    }
}