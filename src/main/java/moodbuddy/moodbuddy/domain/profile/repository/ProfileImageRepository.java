package moodbuddy.moodbuddy.domain.profile.repository;

import moodbuddy.moodbuddy.domain.profile.domain.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    @Query("select pi from ProfileImage pi where pi.userId = :userId")
    Optional<ProfileImage> findByUserId(@Param("userId") Long userId);
}
