package moodbuddy.moodbuddy.external.gpt.service;

import moodbuddy.moodbuddy.domain.diary.domain.Diary;
import moodbuddy.moodbuddy.external.gpt.dto.GptResponseDTO;
import reactor.core.publisher.Mono;

public interface GptService {
    Mono<Void> analyzeDiary(Diary diary);
    GptResponseDTO letterAnswerSave(String worryContent, Integer format);
}
