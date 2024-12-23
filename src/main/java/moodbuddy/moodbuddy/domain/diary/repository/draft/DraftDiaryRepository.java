package moodbuddy.moodbuddy.domain.diary.repository.draft;

import moodbuddy.moodbuddy.domain.diary.domain.Diary;
import moodbuddy.moodbuddy.domain.diary.domain.type.DiaryStatus;
import moodbuddy.moodbuddy.global.common.base.MoodBuddyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DraftDiaryRepository extends JpaRepository<Diary, Long>, DraftDiaryRepositoryCustom {
    Optional<Diary> findByDiaryIdAndDiaryStatusAndMoodBuddyStatus(Long diaryId, DiaryStatus diaryStatus, MoodBuddyStatus moodBuddyStatus);
    List<Diary> findAllByDiaryDateAndUserIdAndDiaryStatus(LocalDate diaryDate, Long userId, DiaryStatus diaryStatus);
}
