package moodbuddy.moodbuddy.domain.letter.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import moodbuddy.moodbuddy.global.common.base.BaseTimeEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "letter")
public class Letter extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false, columnDefinition = "bigint")
    private Long userId;

    @Column(name = "letter_format", nullable = false, columnDefinition = "int")
    private Integer letterFormat;

    @Lob
    @Column(name = "worry_content", columnDefinition = "TEXT")
    private String letterWorryContent;

    @Lob
    @Column(name = "answer_content", columnDefinition = "TEXT")
    private String letterAnswerContent;

    @Column(name = "letter_date", nullable = false, columnDefinition = "datetime")
    private LocalDateTime letterDate;
}