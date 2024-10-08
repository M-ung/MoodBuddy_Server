package moodbuddy.moodbuddy.domain.letter.repository;

import moodbuddy.moodbuddy.domain.letter.domain.Letter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface LetterRepository extends JpaRepository<Letter, Long> {

    @Query("select l from Letter l where l.userId = :userId")
    List<Letter> findLettersByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("update Letter l set l.letterAnswerContent = :answer where l.id = :letterId")
    void updateAnswerByLetterId(@Param("letterId") Long letterId, @Param("answer") String answer);

    @Query("select l from Letter l where l.id = :letterId and l.userId = :userId")
    Optional<Letter> findByIdAndUserId(@Param("letterId") Long letterId, @Param("userId") Long userId);
}