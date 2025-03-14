package moodbuddy.moodbuddy.domain.profile.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import moodbuddy.moodbuddy.global.common.base.BaseTimeEntity;
@Entity
@Table(name = "profile_image")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileImage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", columnDefinition = "bigint")
    private Long userId;

    @Column(name = "profile_img_url", columnDefinition = "varchar(255)")
    private String profileImgURL;

}