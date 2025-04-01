package moodbuddy.moodbuddy.domain.draftDiary.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import moodbuddy.moodbuddy.domain.draftDiary.dto.response.DraftDiaryResDetailDTO;
import moodbuddy.moodbuddy.domain.draftDiary.dto.response.DraftDiaryResFindOneDTO;
import moodbuddy.moodbuddy.domain.draftDiary.exception.DraftDiaryNotFoundException;
import moodbuddy.moodbuddy.global.common.base.type.MoodBuddyStatus;
import moodbuddy.moodbuddy.global.error.ErrorCode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static moodbuddy.moodbuddy.domain.draftDiary.domain.QDraftDiary.draftDiary;
import static moodbuddy.moodbuddy.domain.draftDiary.domain.image.QDraftDiaryImage.draftDiaryImage;

public class DraftDiaryRepositoryImpl implements DraftDiaryRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public DraftDiaryRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<DraftDiaryResFindOneDTO> getDraftDiaries(Long userId) {
        return queryFactory.select(Projections.constructor(DraftDiaryResFindOneDTO.class,
                        draftDiary.id,
                        draftDiary.date
                ))
                .from(draftDiary)
                .where(draftDiary.userId.eq(userId)
                        .and(draftDiary.moodBuddyStatus.eq(MoodBuddyStatus.ACTIVE)))
                .fetch();
    }

    @Override
    public DraftDiaryResDetailDTO getDraftDiaryById(Long userId, Long draftDiaryId) {
        var result = queryFactory.select(
                        draftDiary.id,
                        draftDiary.title,
                        draftDiary.date,
                        draftDiary.content,
                        draftDiary.weather,
                        draftDiary.font,
                        draftDiary.fontSize,
                        draftDiaryImage.imageUrl
                )
                .from(draftDiary)
                .leftJoin(draftDiaryImage)
                .on(draftDiary.id.eq(draftDiaryImage.draftDiaryId)
                        .and(draftDiaryImage.moodBuddyStatus.eq(MoodBuddyStatus.ACTIVE)))
                .where(draftDiary.id.eq(draftDiaryId),
                        draftDiary.userId.eq(userId),
                        draftDiary.moodBuddyStatus.eq(MoodBuddyStatus.ACTIVE))
                .fetch();

        if (result.isEmpty()) {
            throw new DraftDiaryNotFoundException(ErrorCode.DRAFT_DIARY_NOT_FOUND);
        }

        var draftDiaryResDetailDTO = DraftDiaryResDetailDTO.from(result.getFirst());
        draftDiaryResDetailDTO.saveDraftDiaryImageUrls(extractDiaryImages(result));
        return draftDiaryResDetailDTO;
    }
    private List<String> extractDiaryImages(List<Tuple> results) {
        return results.stream()
                .map(tuple -> tuple.get(draftDiaryImage.imageUrl))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
