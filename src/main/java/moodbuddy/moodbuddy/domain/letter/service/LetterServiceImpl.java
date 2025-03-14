package moodbuddy.moodbuddy.domain.letter.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moodbuddy.moodbuddy.domain.letter.dto.request.LetterReqDTO;
import moodbuddy.moodbuddy.domain.letter.dto.request.LetterReqUpdateDTO;
import moodbuddy.moodbuddy.domain.letter.dto.response.*;
import moodbuddy.moodbuddy.domain.letter.domain.Letter;
import moodbuddy.moodbuddy.domain.letter.repository.LetterRepository;
import moodbuddy.moodbuddy.domain.profile.domain.Profile;
import moodbuddy.moodbuddy.domain.profile.domain.ProfileImage;
import moodbuddy.moodbuddy.domain.profile.repository.ProfileImageRepository;
import moodbuddy.moodbuddy.domain.profile.repository.ProfileRepository;
import moodbuddy.moodbuddy.domain.user.domain.User;
import moodbuddy.moodbuddy.domain.user.repository.UserRepository;
import moodbuddy.moodbuddy.global.error.ErrorCode;
import moodbuddy.moodbuddy.domain.letter.exception.LetterNotFoundByIdAndUserIdException;
import moodbuddy.moodbuddy.domain.letter.exception.LetterNotFoundByIdException;
import moodbuddy.moodbuddy.domain.letter.exception.LetterNumsException;
import moodbuddy.moodbuddy.domain.user.exception.profile.ProfileNotFoundByUserIdException;
import moodbuddy.moodbuddy.domain.user.exception.UserNotFoundByUserIdException;
import moodbuddy.moodbuddy.external.gpt.dto.GptMessageDTO;
import moodbuddy.moodbuddy.external.gpt.dto.GptResponseDTO;
import moodbuddy.moodbuddy.external.gpt.service.GptService;
import moodbuddy.moodbuddy.external.sms.SmsService;
import moodbuddy.moodbuddy.global.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LetterServiceImpl implements LetterService {
    private static final int MIN_LETTER_NUMS = 0;
    private static final int SCHEDULED_DELAY = 25000;

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ProfileImageRepository profileImageRepository;
    private final LetterRepository letterRepository;
    private final GptService gptService;
    private final SmsService smsService;

    @Override
    @Transactional(timeout = 30)
    public LetterResPageDTO letterPage() {
        log.info("[LetterService] letterPage");
        final Long userId = JwtUtil.getUserId();
        try {
            User user = getUserByUserId(userId);
            initLetterAlarmFromUser(user, userId);

            return createLetterResPageDto(userId, user);
        } catch (Exception e) {
            log.error("[LetterService] letterPage error", e);
            throw new RuntimeException("[LetterService] letterPage error", e);
        }
    }


    private Profile getProfileByUserId(Long userId){
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundByUserIdException(ErrorCode.PROFILE_NOT_FOUND));
    }

    private String getProfileImageUrlByUserId(Long userId){
        return profileImageRepository.findByUserId(userId)
                .map(ProfileImage::getProfileImgURL)
                .orElse("");
    }

    private List<LetterResPageAnswerDTO> getLetterResPageAnswerDTOListForLetterPage(Long userId){
        List<Letter> letters = letterRepository.findLettersByUserId(userId);
        return letters.stream()
                .map(letter -> LetterResPageAnswerDTO.builder()
                        .letterId(letter.getId())
                        .letterDate(letter.getLetterDate())
                        .answerCheck(letter.getLetterAnswerContent() != null ? 1 : 0)
                        .build())
                .collect(Collectors.toList());
    }

    private LetterResPageDTO createLetterResPageDto(Long userId, User user) {
        Profile profile = getProfileByUserId(userId);
        String profileImageURL = getProfileImageUrlByUserId(userId);
        List<LetterResPageAnswerDTO> letterResPageAnswerDTOList = getLetterResPageAnswerDTOListForLetterPage(user.getId());

        return getLetterResPageDtoForLetterPage(user, profile, profileImageURL, letterResPageAnswerDTOList);
    }

    private void initLetterAlarmFromUser(User user, Long userId){
        if(user.getLetterAlarm()==null){
            userRepository.updateLetterAlarmByUserId(userId, false);
        }
    }

    private LetterResPageDTO getLetterResPageDtoForLetterPage(User user, Profile profile, String profileImageURL, List<LetterResPageAnswerDTO> letterResPageAnswerDTOList){
        return LetterResPageDTO.builder()
                .nickname(user.getNickname())
                .userBirth(user.getBirthday())
                .profileComment(profile.getProfileComment())
                .profileImageUrl(profileImageURL)
                .userLetterNums(user.getUserLetterNums())
                .letterAlarm(user.getLetterAlarm())
                .letterResPageAnswerDTOList(letterResPageAnswerDTOList)
                .build();
    }

    @Override
    @Transactional
    public LetterResUpdateDTO updateLetterAlarm(LetterReqUpdateDTO letterReqUpdateDTO){
        log.info("[LetterService] updateLetterAlarm");
        try{
            final Long userId = JwtUtil.getUserId();

            User user = getUserByUserIdWithPessimisticLock(userId);
            user.setLetterAlarm(letterReqUpdateDTO.getLetterAlarm());

            return getLetterResUpdateDtoForUpdateLetterAlarm(user);
        } catch (Exception e){
            log.error("[LetterService] updateLetterAlarm error", e);
            throw new RuntimeException("[LetterService] updateLetterAlarm error", e);
        }
    }

    private LetterResUpdateDTO getLetterResUpdateDtoForUpdateLetterAlarm(User user){
        return LetterResUpdateDTO.builder()
                .nickname(user.getNickname())
                .letterAlarm(user.getLetterAlarm())
                .build();
    }

    @Override
    @Transactional
    public LetterResSaveDTO letterSave(LetterReqDTO letterReqDTO) {
        log.info("[LetterService] save");
        final Long userId = JwtUtil.getUserId();
        try {
            User user = getUserByUserIdWithPessimisticLock(userId);
            validateUserLetterAvailability(user);

            User updatedUser = updateUserLetterNums(user);
            Letter letter = SaveLetter(updatedUser, letterReqDTO);
            LetterResSaveDTO letterResSaveDTO = buildLetterResSaveDtoForLetterSave(updatedUser, letter);

            // 30초 후에 letterAnswerSave 호출 예약
            scheduleLetterAnswerSave(userId, letterResSaveDTO);

            return letterResSaveDTO;
        } catch (Exception e) {
            log.error("[LetterService] save error", e);
            throw new RuntimeException("[LetterService] save error", e);
        }
    }

    private void validateUserLetterAvailability(User user){
        log.info("user.getUserLetterNums() : "+user.getUserLetterNums());
        if (user.getUserLetterNums() == null || user.getUserLetterNums() <= MIN_LETTER_NUMS) {
            throw new LetterNumsException(ErrorCode.LETTER_INVALID_NUMS); // 편지지가 없을 경우 예외 처리
        }
    }

    private User updateUserLetterNums(User user){
        // 편지지 개수 업데이트
        user.setUserLetterNums(user.getUserLetterNums() - 1);
        return userRepository.save(user);
    }

    private Letter SaveLetter(User user, LetterReqDTO letterReqDTO){
        return letterRepository.save(Letter.builder()
                .userId(user.getId())
                .letterFormat(letterReqDTO.getLetterFormat())
                .letterWorryContent(letterReqDTO.getLetterWorryContent())
                .letterDate(letterReqDTO.getLetterDate())
                .build());
    }

    private LetterResSaveDTO buildLetterResSaveDtoForLetterSave(User user, Letter letter){
        return LetterResSaveDTO.builder()
                .letterId(letter.getId())
                .userNickname(user.getNickname())
                .letterDate(letter.getLetterDate())
                .build();
    }

    private void scheduleLetterAnswerSave(Long userId, LetterResSaveDTO letterResSaveDTO) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 30초 후에 실행할 작업
                letterAnswerSave(userId, letterResSaveDTO);
            }
        }, SCHEDULED_DELAY); // 25초 지연
    }

    @Override
    @Transactional(timeout = 30)
    public void letterAnswerSave(Long userId, LetterResSaveDTO letterResSaveDTO) {
        log.info("[LetterService] answerSave");
        try {
            User user = getUserByUserIdWithPessimisticLock(userId);
            Letter letter = getLetterById(letterResSaveDTO.getLetterId());

            // GPT의 응답을 가져옴
            GptResponseDTO response = getGPTResponseDto(letter);

            // 유저의 알림 설정에 따라 메시지를 전송
            sendMessageIfEnabled(user);

            // GPT 응답을 통해 편지 답변을 업데이트
            updateAnswerFromGptResponse(response, letterResSaveDTO);
        } catch (Exception e) {
            log.error("[LetterService] answerSave error", e);
        }
    }

    private Letter getLetterById(Long letterId){
        return letterRepository.findById(letterId)
                .orElseThrow(()->new LetterNotFoundByIdException(ErrorCode.LETTER_NOT_FOUND_BY_ID));
    }

    private GptResponseDTO getGPTResponseDto(Letter letter){
        return gptService.letterAnswerSave(letter.getLetterWorryContent(), letter.getLetterFormat());
    }

    private void updateAnswerFromGptResponse(GptResponseDTO response, LetterResSaveDTO letterResSaveDTO){
        if (response != null && response.getChoices() != null) {
            for (GptResponseDTO.Choice choice : response.getChoices()) {
                GptMessageDTO message = choice.getMessage();
                if (message != null) {
                    String answer = message.getContent();
                    letterRepository.updateAnswerByLetterId(letterResSaveDTO.getLetterId(), answer);
                }
            }
        } else {
            log.error("GPT 응답 오류");
        }
    }

    private void sendMessageIfEnabled(User user) {
        if (user.getLetterAlarm() && !user.getPhoneNumber().isEmpty()) {
            smsService.sendMessage(user.getPhoneNumber(),"LETTER");
        }
    }

    @Override
    @Transactional(readOnly = true, timeout = 30)
    public LetterResDetailsDTO letterDetails(Long letterId) {
        log.info("[LetterService] details");
        try {
            final Long userId = JwtUtil.getUserId();

            User user = getUserByUserId(userId);
            Letter letter = getLetterByIdAndUserId(letterId, user.getId());

            return getLetterResDetailsDtoForLetterDetails(user, letter);
        } catch (Exception e) {
            log.error("[LetterService] details error", e);
            throw new RuntimeException("[LetterService] details error", e);
        }
    }

    private User getUserByUserId(Long userId){
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundByUserIdException(ErrorCode.USER_NOT_FOUND));
    }

    private Letter getLetterByIdAndUserId(Long letterId, Long userId){
        return letterRepository.findByIdAndUserId(letterId, userId)
                .orElseThrow(() -> new LetterNotFoundByIdAndUserIdException(ErrorCode.LETTER_NOT_FOUND_BY_ID_AND_USER_ID));
    }

    private LetterResDetailsDTO getLetterResDetailsDtoForLetterDetails(User user, Letter letter){
        return LetterResDetailsDTO.builder()
                .letterId(letter.getId())
                .userNickname(user.getNickname())
                .letterFormat(letter.getLetterFormat())
                .letterWorryContent(letter.getLetterWorryContent())
                .letterAnswerContent(letter.getLetterAnswerContent())
                .letterDate(letter.getLetterDate())
                .build();
    }

    private User getUserByUserIdWithPessimisticLock(Long userId){
        return userRepository.findByUserIdWithPessimisticLock(userId).orElseThrow(
                () -> new UserNotFoundByUserIdException(ErrorCode.USER_NOT_FOUND)
        );
    }

    // 비동기적으로 실행되는 작업이 현재 요청의 데이터를 액세스하고 처리하게 하는 클래스
    public static class ContextAwareRunnable implements Runnable {

        private final Runnable task;
        private final RequestAttributes context;

        public ContextAwareRunnable(Runnable task) {
            this.task = task;
            this.context = RequestContextHolder.currentRequestAttributes();
        }

        @Override
        public void run() {
            try {
                RequestContextHolder.setRequestAttributes(context);
                task.run();
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        }
    }
}
