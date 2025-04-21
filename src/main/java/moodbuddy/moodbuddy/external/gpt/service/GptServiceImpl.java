package moodbuddy.moodbuddy.external.gpt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import moodbuddy.moodbuddy.domain.diary.domain.Diary;
import moodbuddy.moodbuddy.domain.diary.repository.DiaryRepository;
import moodbuddy.moodbuddy.external.gpt.exception.GptAnalyzeFailException;
import moodbuddy.moodbuddy.global.error.ErrorCode;
import moodbuddy.moodbuddy.external.gpt.exception.ParsingContentException;
import moodbuddy.moodbuddy.external.gpt.dto.GptRequestDTO;
import moodbuddy.moodbuddy.external.gpt.dto.GptResponseDTO;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GptServiceImpl implements GptService{
    private final WebClient gptWebClient;
    private final DiaryRepository diaryRepository;

    @Value("${gpt.model}")
    private String model;

    @Value("${gpt.api.url}")
    private String apiUrl;

    private static final String FULL_ANALYSIS_PROMPT = "다음 일기를 한글로 분석해줘. 주제는 DAILY, GROWTH, EMOTION, TRAVEL 중 하나, 요약은 한 문장, 감정은 HAPPINESS, ANGER, DISGUST, FEAR, NEUTRAL, SADNESS, SURPRISE 중 하나, 감정 코멘트는 20자. 응답 형식은 JSON으로: {\"subject\":\"\", \"summary\":\"\", \"emotion\":\"\", \"comment\":\"\"}";

    // 따뜻한 위로의 쿼디 답변 프롬프트
    private static final String GENTLE_LETTER_ANSWER_PROMPT = " 이 내용에 대해 편한 친구같은 느낌으로 따뜻한 위로의 답변을 해줘. 이 때 답변을 너무 건방지지 않고 부드럽게, 친구같이 편한 반말로 해줘";
    // 따끔한 해결의 쿼디 답변 프롬프트
    private static final String STERN_LETTER_ANSWER_PROMPT = " 이 내용에 대해 편한 친구 같은 느낌으로 따끔한 해결의 답변을 해줘. 이 때 답변을 너무 건방지지 않고 부드럽게, 친구같이 편한 반말로 해줘";
    private static final int RETRY_ATTEMPTS = 1;
    private static final int RETRY_DELAY_SECONDS = 1;

    public GptServiceImpl(@Qualifier("gptWebClient") WebClient gptWebClient, DiaryRepository diaryRepository) {
        this.gptWebClient = gptWebClient;
        this.diaryRepository = diaryRepository;
    }

    @Override
    @Transactional
    public Mono<Void> analyzeDiary(Diary diary) {
        List<String> keys = List.of("subject", "summary", "emotion", "comment");
        GptRequestDTO request = new GptRequestDTO(model, diary.getContent() + FULL_ANALYSIS_PROMPT);

        return gptWebClient.post()
                .uri(apiUrl)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GptResponseDTO.class)
                .retryWhen(Retry.backoff(RETRY_ATTEMPTS, Duration.ofSeconds(RETRY_DELAY_SECONDS)))
                .flatMap(res -> {
                    try {
                        String content = res.getChoices().getFirst().getMessage().getContent();
                        Map<String, String> parsed = parseJsonContent(content, keys);
                        diary.analyzeDiaryResult(parsed);
                        diaryRepository.save(diary);
                        return Mono.<Void>empty();
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                })
                .onErrorResume(ex -> {
                    log.error("[GPT 분석 실패 - fallback 응답 반환]", ex);
                    diary.analyzeDiaryResult(getFallbackParsedMap());
                    diaryRepository.save(diary);
                    return Mono.error(new GptAnalyzeFailException(ErrorCode.GPT_BAD_REQUEST));
                });
    }

    private static Map<String, String> getFallbackParsedMap() {
        return Map.of(
                "subject", "FAILED",
                "summary", "요약 실패",
                "emotion", "FAILED",
                "comment", "감정 분석 실패"
        );
    }

    private Map<String, String> parseJsonContent(String content, List<String> keys) {
        Map<String, String> responseMap = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(content);
            for (String key : keys) {
                responseMap.put(key, jsonNode.path(key).asText());
            }
        } catch (Exception e) {
            throw new ParsingContentException(ErrorCode.GPT_PARSE_ERROR);
        }
        return responseMap;
    }

    @Override
    public GptResponseDTO letterAnswerSave(String worryContent, Integer format){
        return getResponseForLetterAnswerSaveMethod(new GptRequestDTO(model, worryContent + (format == 1 ? GENTLE_LETTER_ANSWER_PROMPT : STERN_LETTER_ANSWER_PROMPT)));
    }

    private GptResponseDTO getResponseForLetterAnswerSaveMethod(GptRequestDTO gptRequestDTO){
        return gptWebClient.post()
                .uri(apiUrl)
                .bodyValue(gptRequestDTO)
                .retrieve()
                .bodyToMono(GptResponseDTO.class)
                .retryWhen(Retry.backoff(RETRY_ATTEMPTS, Duration.ofSeconds(RETRY_DELAY_SECONDS)))
                .block();
    }
}