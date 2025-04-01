package moodbuddy.moodbuddy.domain.diary.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import moodbuddy.moodbuddy.domain.diary.dto.response.DiaryResDetailDTO;
import moodbuddy.moodbuddy.domain.diary.exception.DiaryNotFoundException;
import moodbuddy.moodbuddy.global.common.base.type.MoodBuddyStatus;
import moodbuddy.moodbuddy.global.error.ErrorCode;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static moodbuddy.moodbuddy.domain.diary.domain.QDiary.diary;
import static moodbuddy.moodbuddy.domain.diary.domain.image.QDiaryImage.diaryImage;

public class DiaryRepositoryImpl implements DiaryRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    public DiaryRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public DiaryResDetailDTO getDiaryById(final Long userId, final Long diaryId) {
        var result = queryFactory.select(
                        diary.id,
                        diary.title,
                        diary.date,
                        diary.content,
                        diary.weather,
                        diary.emotion,
                        diary.font,
                        diary.fontSize,
                        diaryImage.imageUrl
                )
                .from(diary)
                .leftJoin(diaryImage)
                .on(diary.id.eq(diaryImage.diaryId)
                        .and(diaryImage.moodBuddyStatus.eq(MoodBuddyStatus.ACTIVE)))
                .where(diary.id.eq(diaryId),
                        diary.moodBuddyStatus.eq(MoodBuddyStatus.ACTIVE)
                        ,diary.userId.eq(userId))
                .fetch();

        if (result.isEmpty()) {
            throw new DiaryNotFoundException(ErrorCode.DIARY_NOT_FOUND);
        }

        var diaryResDetailDTO = DiaryResDetailDTO.from(result.getFirst());
        diaryResDetailDTO.saveDiaryImageUrls(extractDiaryImages(result));
        return diaryResDetailDTO;
    }

    private List<String> extractDiaryImages(List<Tuple> results) {
        return results.stream()
                .map(tuple -> tuple.get(diaryImage.imageUrl))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}