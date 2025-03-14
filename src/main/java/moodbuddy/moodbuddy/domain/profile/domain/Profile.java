package moodbuddy.moodbuddy.domain.profile.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import moodbuddy.moodbuddy.global.common.base.BaseTimeEntity;

@Entity
@Table(name = "profile")
@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
public class Profile extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "profile_comment", columnDefinition = "varchar(255)")
    private String profileComment;

    @Column(name = "user_id", columnDefinition = "bigint")
    private Long userId;

    protected Profile() {}
}