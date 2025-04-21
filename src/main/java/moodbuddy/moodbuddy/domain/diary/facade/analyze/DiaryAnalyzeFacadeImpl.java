package moodbuddy.moodbuddy.domain.diary.facade.analyze;

import lombok.RequiredArgsConstructor;
import moodbuddy.moodbuddy.domain.diary.service.DiaryService;
import moodbuddy.moodbuddy.external.gpt.service.GptService;
import moodbuddy.moodbuddy.global.util.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryAnalyzeFacadeImpl implements DiaryAnalyzeFacade {
    private final DiaryService diaryService;
    private final GptService gptService;

    @Override
    @Transactional
    public Mono<Void> analyze(Long diaryId) {
        final var userId = JwtUtil.getUserId();
        return gptService.analyzeDiary(diaryService.findDiaryById(userId, diaryId));
    }
}