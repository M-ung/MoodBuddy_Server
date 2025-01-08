package moodbuddy.moodbuddy.domain.bookMark.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import moodbuddy.moodbuddy.domain.diary.dto.response.query.DiaryResQueryDTO;
import moodbuddy.moodbuddy.global.common.base.type.MoodBuddyStatus;
import moodbuddy.moodbuddy.global.common.base.PageCustom;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import static moodbuddy.moodbuddy.domain.diary.domain.QDiary.diary;

public class BookMarkRepositoryImpl implements BookMarkRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    public BookMarkRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public PageCustom<DiaryResQueryDTO> getBookMarksWithPageable(Long userId, Pageable pageable) {
        var results = queryFactory.select(Projections.constructor(DiaryResQueryDTO.class,
                        diary.id,
                        diary.title,
                        diary.date,
                        diary.content,
                        diary.thumbnail
                ))
                .from(diary)
                .where(diary.userId.eq(userId)
                        .and(diary.bookMark.eq(true))
                        .and(diary.moodBuddyStatus.eq(MoodBuddyStatus.ACTIVE)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        var total = getTotal(userId);
        var totalPages = (int) Math.ceil((double) total / pageable.getPageSize());

        return new PageCustom<>(results, totalPages, total, pageable.getPageSize(), pageable.getPageNumber());
    }

    private long getTotal(Long userId) {
        return Optional.ofNullable(
                queryFactory.select(diary.count())
                        .from(diary)
                        .where(diary.userId.eq(userId)
                                .and(diary.moodBuddyStatus.eq(MoodBuddyStatus.ACTIVE)))
                        .fetchOne()
        ).orElse(0L);
    }
}
