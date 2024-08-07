package moodbuddy.moodbuddy.domain.gpt.service;

import moodbuddy.moodbuddy.domain.gpt.dto.GPTResponseDTO;
import reactor.core.publisher.Mono;

public interface GptService {
    Mono<String> classifyDiaryContent(String content);

    Mono<String> summarize(String content);

    Mono<GPTResponseDTO> letterAnswerSave(String worryContent, Integer format);

    Mono<String> emotionComment(String emotion);
}
