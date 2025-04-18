package moodbuddy.moodbuddy.external.gpt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import moodbuddy.moodbuddy.domain.diary.domain.Diary;
import moodbuddy.moodbuddy.domain.diary.domain.type.DiaryEmotion;
import moodbuddy.moodbuddy.domain.diary.domain.type.DiarySubject;
import moodbuddy.moodbuddy.domain.diary.dto.response.emotion.DiaryResAnalyzeDTO;
import moodbuddy.moodbuddy.global.error.ErrorCode;
import moodbuddy.moodbuddy.external.gpt.exception.ParsingContentException;
import moodbuddy.moodbuddy.external.gpt.dto.GptRequestDTO;
import moodbuddy.moodbuddy.external.gpt.dto.GptResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GptServiceImpl implements GptService{
    private final WebClient gptWebClient;

    @Value("${gpt.model}")
    private String model;

    @Value("${gpt.api.url}")
    private String apiUrl;

    private static final String FULL_ANALYSIS_PROMPT = "다음 일기를 한글로 분석해줘. 주제는 DAILY, GROWTH, EMOTION, TRAVEL 중 하나, 요약은 한 문장, 감정은 HAPPINESS, ANGER, DISGUST, FEAR, NEUTRAL, SADNESS, SURPRISE 중 하나, 감정 코멘트는 20자. 응답 형식은 JSON으로: {\"subject\":\"\", \"summary\":\"\", \"emotion\":\"\", \"comment\":\"\"}";

//    // 일기 주제 + 요약 프롬프트
//    private static final String CONTENT_ANALYSIS_PROMPT = " 이 일기 내용을 분석하여, 주제에 해당하는 값을 다음 중에서 선택해 줘: \"일상\", \"성장\", \"감정\", \"여행\". 주제 값은 \"DAILY\", \"GROWTH\", \"EMOTION\", \"TRAVEL\" 중 하나로 출력해 줘." +
//            " 그리고 이 일기를 서술형인 한 문장으로 요약해 주고, 요약 내용은 반드시 한 문장이어야 하며, 무조건 요약한 내용만 출력해 줘." +
//            " 마지막으로, 두 가지 응답을 다른 설명 없이 다음 형식으로 반환해 줘: { \"subject\": \"주제 값\", \"summary\": \"요약 내용\" }.";
//    // 일기 감정 + 감정 한 마디 프롬프트
//    private static final String EMOTION_ANALYSIS_PROMPT = " 이 일기 내용을 분석하여, 일기에서 느껴지는 감정을 \"HAPPINESS\", \"ANGER\", \"DISGUST\", \"FEAR\", \"NEUTRAL\", \"SADNESS\", \"SURPRISE\" 중에서 골라줘. " +
//            " 그리고 이 감정에 따른 한 줄 코멘트를 남겨줘. 글자 수는 20자로 제한하며, 꼭 한 줄로 작성해 줘." +
//            " 마지막으로, 두 가지 응답을 다른 설명 없이 다음 형식으로 반환해 줘: { \"diaryEmotion\": \"감정 값\", \"diaryComment\": \"한 줄 코멘트\" }.";
    // 따뜻한 위로의 쿼디 답변 프롬프트
    private static final String GENTLE_LETTER_ANSWER_PROMPT = " 이 내용에 대해 편한 친구같은 느낌으로 따뜻한 위로의 답변을 해줘. 이 때 답변을 너무 건방지지 않고 부드럽게, 친구같이 편한 반말로 해줘";
    // 따끔한 해결의 쿼디 답변 프롬프트
    private static final String STERN_LETTER_ANSWER_PROMPT = " 이 내용에 대해 편한 친구 같은 느낌으로 따끔한 해결의 답변을 해줘. 이 때 답변을 너무 건방지지 않고 부드럽게, 친구같이 편한 반말로 해줘";
    private static final int RETRY_ATTEMPTS = 3;
    private static final int RETRY_DELAY_SECONDS = 5;

    public GptServiceImpl(@Qualifier("gptWebClient") WebClient gptWebClient) {
        this.gptWebClient = gptWebClient;
    }


    public DiaryResAnalyzeDTO analyzeDiary(Diary diary) {
        List<String> keys = List.of("subject", "summary", "emotion", "comment");
        Map<String, String> gptResponse = getGPTResponseMap(
                new GptRequestDTO(model, diary.getContent() + FULL_ANALYSIS_PROMPT),
                keys
        );

        diary.analyzeDiaryResult(gptResponse);

        return DiaryResAnalyzeDTO.builder()
                .diarySubject(DiarySubject.valueOf(gptResponse.get("subject")))
                .diarySummary(gptResponse.get("summary"))
                .diaryEmotion(DiaryEmotion.valueOf(gptResponse.get("emotion")))
                .diaryComment(gptResponse.get("comment"))
                .build();
    }

//    @Override
//    public Map<String, String> analyzeDiaryContent(String diaryContent){
//        List<String> keys = List.of("subject", "summary");
//        return getGPTResponseMap(new GptRequestDTO(model, diaryContent + CONTENT_ANALYSIS_PROMPT), keys);
//    }
//
//    @Override
//    public DiaryResAnalyzeDTO analyzeEmotion(){
//        Diary diary = diaryRepository.findDiarySummaryById(JwtUtil.getUserId())
//                .orElseThrow(() -> new DiaryNotFoundException(ErrorCode.DIARY_NOT_FOUND));
//
//        List<String> keys = List.of("diaryEmotion", "diaryComment");
//        Map<String, String> responseMap = getGPTResponseMap(new GptRequestDTO(model, diary.getContent() + EMOTION_ANALYSIS_PROMPT), keys);
//
//        diary.updateDiaryEmotion(DiaryEmotion.valueOf(responseMap.get("diaryEmotion")));
//        diaryRepository.save(diary);
//
//        return DiaryResAnalyzeDTO.builder()
//                .diaryEmotion(responseMap.get("diaryEmotion"))
//                .diaryDate(diary.getDate())
//                .diaryComment(responseMap.get("diaryComment"))
//                .build();
//    }

    private Map<String, String> getGPTResponseMap(GptRequestDTO gptRequestDTO, List<String> keys) {
        return gptWebClient.post()
                .uri(apiUrl)
                .bodyValue(gptRequestDTO)
                .retrieve()
                .bodyToMono(GptResponseDTO.class)
                .map(gptResponseDTO -> {
                    String content = gptResponseDTO.getChoices().get(0).getMessage().getContent();
                    return parseJsonContent(content, keys);
                })
                .block();
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