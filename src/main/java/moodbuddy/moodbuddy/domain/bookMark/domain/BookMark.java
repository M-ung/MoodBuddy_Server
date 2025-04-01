package moodbuddy.moodbuddy.domain.bookMark.domain;

import jakarta.persistence.*;
import lombok.*;
import moodbuddy.moodbuddy.global.common.base.BaseTimeEntity;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "book_mark",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "diary_id"})
        }
)
public class BookMark extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "diary_id")
    private Long diaryId;

    public static BookMark of(Long userId, Long diaryId) {
        return BookMark.builder()
                .userId(userId)
                .diaryId(diaryId)
                .build();
    }
}