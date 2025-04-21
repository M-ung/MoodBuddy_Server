package moodbuddy.moodbuddy.domain.diary.facade.analyze;

import reactor.core.publisher.Mono;

public interface DiaryAnalyzeFacade {
    Mono<Void> analyze(Long diaryId);
}