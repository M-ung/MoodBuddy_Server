package moodbuddy.moodbuddy.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moodbuddy.moodbuddy.domain.diary.domain.Diary;
import moodbuddy.moodbuddy.domain.diary.domain.type.DiaryEmotion;
import moodbuddy.moodbuddy.domain.diary.repository.DiaryRepository;
import moodbuddy.moodbuddy.domain.monthcomment.domain.MonthComment;
import moodbuddy.moodbuddy.domain.monthcomment.repository.MonthCommentRepository;
import moodbuddy.moodbuddy.domain.profile.domain.Profile;
import moodbuddy.moodbuddy.domain.profile.dto.request.ProfileReqUpdateDTO;
import moodbuddy.moodbuddy.domain.profile.dto.response.ProfileResDetailDTO;
import moodbuddy.moodbuddy.domain.profile.repository.ProfileRepository;
import moodbuddy.moodbuddy.domain.profile.domain.ProfileImage;
import moodbuddy.moodbuddy.domain.profile.repository.ProfileImageRepository;
import moodbuddy.moodbuddy.global.error.ErrorCode;
import moodbuddy.moodbuddy.domain.user.exception.profile.ProfileImageNotFoundByUserIdException;
import moodbuddy.moodbuddy.domain.user.exception.profile.ProfileNotFoundByUserIdException;
import moodbuddy.moodbuddy.domain.user.exception.UserNotFoundByUserIdException;
import moodbuddy.moodbuddy.external.sms.SmsService;
import moodbuddy.moodbuddy.domain.user.dto.request.*;
import moodbuddy.moodbuddy.domain.user.dto.response.UserResCalendarMonthDTO;
import moodbuddy.moodbuddy.domain.user.dto.response.UserResCalendarMonthListDTO;
import moodbuddy.moodbuddy.domain.user.dto.response.UserResCalendarSummaryDTO;
import moodbuddy.moodbuddy.domain.user.dto.response.UserResMainPageDTO;
import moodbuddy.moodbuddy.domain.user.dto.response.*;
import moodbuddy.moodbuddy.domain.user.domain.User;
import moodbuddy.moodbuddy.domain.user.mapper.UserMapper;
import moodbuddy.moodbuddy.domain.user.repository.UserRepository;
import moodbuddy.moodbuddy.global.util.JwtUtil;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ProfileImageRepository profileImageRepository;
    private final DiaryRepository diaryRepository;
    private final MonthCommentRepository monthCommentRepository;
    private final ScheduledExecutorService scheduledExecutorService;
    private final SmsService smsService;

    /** =========================================================  재민  ========================================================= **/

    @Override
    @Transactional
    public UserResMainPageDTO mainPage(){
        try {
            Long userId = JwtUtil.getUserId();
            Optional<User> optionalUser = userRepository.findByUserId(userId);

            // 조회한 유저의 user_id를 통해 profileRepository에서 유저 프로필 조회 (Optional 사용)
            Optional<Profile> optionalProfile = profileRepository.findByUserId(userId);

            if (optionalUser.isPresent() && optionalProfile.isPresent()) {
                // profile_id를 통해 profileImageRepository에서 유저 프로필 이미지 조회 (Optional 사용)
                Optional<ProfileImage> optionalProfileImage = profileImageRepository.findByUserId(userId);
                String profileImgURL = optionalProfileImage.map(ProfileImage::getProfileImgURL).orElse("");

                // 현재 달 계산
                YearMonth yearMonth = YearMonth.now();
                String currentYearMonth = yearMonth.toString();

                // 현재 달의 일기 리스트
                List<Diary> diaryList = diaryRepository.findByUserIdAndMonthAndDiaryStatus(userId, currentYearMonth, "PUBLISHED");

                log.info("diaryList : "+diaryList);

                // 감정 데이터 초기화
                DiaryEmotion diaryEmotion = null;
                int maxEmotionNum = 0;

                if (!diaryList.isEmpty()) {
                    // 횟수가 최댓값인 emotion과 그 값을 저장하기 위한 Map
                    Map<DiaryEmotion, Integer> emotionMap = emotionNum(diaryList);
                    log.info("emotionMap : "+emotionMap);

                    // Map에서 key와 value 가져오기
                    if (!emotionMap.isEmpty()) {
                        diaryEmotion = emotionMap.keySet().iterator().next(); // key가 하나밖에 없기 때문에 iterator().next() 사용
                        maxEmotionNum = emotionMap.get(diaryEmotion);
                    }
                }

                return UserResMainPageDTO.builder()
                        .nickname(optionalUser.get().getNickname())
                        .userBirth(optionalUser.get().getBirthday())
                        .profileComment(optionalProfile.get().getProfileComment())
                        .profileImgURL(profileImgURL)
                        .userCurDiaryNums(optionalUser.get().getUserCurDiaryNums())
                        .diaryEmotion(diaryEmotion)
                        .maxEmotionNum(maxEmotionNum)
                        .build();
            } else {
                throw new RuntimeException("유저와 프로필이 없습니다.");
            }
        } catch (Exception e){
            log.error("[UserService] mainPage error",e);
            throw new RuntimeException("[UserService] mainPage error",e);
        }
    }


    // 각 emotion의 횟수를 세는 메소드
    public Map<DiaryEmotion,Integer> emotionNum(List<Diary> diaryList){
        try{
            // 각 Diary의 emotion을 통해 한 달의 diaryEmotion 횟수를 세기 위한 Map
            Map<DiaryEmotion,Integer> emotionNum = new HashMap<>();
            for(Diary d : diaryList){
                // emotionNum에 현재 Diary의 key가 없다면, key의 value를 1로 설정
                // 이미 현재 Diary의 key가 있다면, 그 key의 value를 + 1
                log.info("d.getDiaryEmotion() : "+d.getEmotion());
                emotionNum.merge(d.getEmotion(), 1, Integer::sum);
            }
            log.info("emotionNum : "+emotionNum);
            // diaryEmotion 개수 중 최댓값 찾기
            return getMaxEmotion(emotionNum);
        } catch (Exception e){
            log.error("[UserService] emotionNum error",e);
            throw new RuntimeException("[UserService] emotionNum error",e);
        }
    }

    // diaryEmotion 횟수의 최댓값을 찾기 위한 메소드
    public Map<DiaryEmotion, Integer> getMaxEmotion(Map<DiaryEmotion, Integer> emotionNum) {
        try{
            int maxValue = 0;
            DiaryEmotion maxKey = null;
            for(Map.Entry<DiaryEmotion,Integer> entry : emotionNum.entrySet()){
                if(entry.getValue() > maxValue){
                    maxKey = entry.getKey();
                    maxValue = entry.getValue();
                }
            }

            log.info("maxKey : "+maxKey);
            log.info("maxValue : "+maxValue);
            // diaryEmotion 개수 중 최댓값의 emotion과 그 값을 저장할 Map
            Map<DiaryEmotion,Integer> maxEmotion = new HashMap<>();
            if(maxKey != null){
                maxEmotion.put(maxKey, maxValue);
            }
            return maxEmotion;
        } catch (Exception e){
            log.error("[UserService] getMaxEmotion error",e);
            throw new RuntimeException("[UserService] getMaxEmotion error",e);
        }
    }

    @Override
    @Transactional
    public UserResCalendarMonthListDTO monthlyCalendar(UserReqCalendarMonthDTO calendarMonthDTO){
        try{
            // -> userID 가져오기
            Long userId = JwtUtil.getUserId();

            // calendarMonthDTO에서 month 가져오기
            // user_id에 맞는 List<Diary> 중에서, month에서 DateTimeFormatter의 ofPattern을 이용한 LocalDateTime 파싱을 통해 년, 월을 얻어오고,
            // repository 에서는 LIKE 연산자를 이용해서 그 년, 월에 맞는 List<Diary>를 얻어온다
            // (여기서 user_id에 맞는 리스트 전체 조회를 하지 말고, user_id와 년 월에 맞는 리스트만 조회하자)
            // -> 그 Diary 리스트를 그대로 DTO에 넣어서 반환해주면 될 것 같다.
            List<Diary> monthlyDiaryList = diaryRepository.findByUserIdAndMonthAndDiaryStatus(userId, calendarMonthDTO.getCalendarMonth(), "PUBLISHED");

            List<UserResCalendarMonthDTO> diaryResCalendarMonthDTOList = monthlyDiaryList.stream()
                    .map(diary -> UserResCalendarMonthDTO.builder()
                            .diaryId(diary.getId())
                            .diaryDate(diary.getDate())
                            .diaryEmotion(diary.getEmotion())
                            .build())
                    .collect(Collectors.toList());

            return UserResCalendarMonthListDTO.builder()
                    .diaryResCalendarMonthDTOList(diaryResCalendarMonthDTOList)
                    .build();
        } catch (Exception e) {
            log.error("[UserService] monthlyCalendar error", e);
            throw new RuntimeException("[UserService] monthlyCalendar error", e);
        }
    }

    @Override
    @Transactional
    public UserResCalendarSummaryDTO summary(UserReqCalendarSummaryDTO calendarSummaryDTO) {
        try {
            Long userId = JwtUtil.getUserId();

            // userEmail와 calendarSummaryDTO에서 가져온 day와 일치하는 Diary 하나를 가져온다.
            Optional<Diary> summaryDiary = diaryRepository.findByUserIdAndDayAndDiaryStatus(userId, calendarSummaryDTO.getCalendarDay(), "PUBLISHED");

            // summaryDiary가 존재하면 그에 맞게 DTO를 build하여 반환하고, 그렇지 않으면 빈 DTO를 반환한다.
            return summaryDiary.map(diary -> UserResCalendarSummaryDTO.builder()
                            .diaryId(diary.getId())
                            .diaryTitle(diary.getTitle())
                            .diarySummary(diary.getSummary())
                            .build())
                    .orElse(UserResCalendarSummaryDTO.builder().build());
        } catch(Exception e){
            log.error("[UserService] summary NoSuchElementException", e);
            throw new RuntimeException("[UserService] summary NoSuchElementException", e);
        }
    }

    @Override
    public void scheduleUserMessage(Long userId) {
        try {
            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
            String alarmTimeString = user.getAlarmTime();
            LocalTime alarmTime = LocalTime.parse(alarmTimeString, DateTimeFormatter.ofPattern("HH:mm"));
            LocalDateTime alarmDateTime = LocalDateTime.now().with(alarmTime);

            if (alarmDateTime.isBefore(LocalDateTime.now())) {
                alarmDateTime = alarmDateTime.plusDays(1);
            }

            long delay = Duration.between(LocalDateTime.now(), alarmDateTime).toMillis();

            scheduledExecutorService.schedule(() -> {
                sendUserMessage(user);
                // 다음 날 동일 시간에 다시 스케줄링
                scheduleUserMessage(userId);
            }, delay, TimeUnit.MILLISECONDS);

        } catch (Exception e) {
            log.error("[UserService] scheduleUserMessage error", e);
        }
    }

    private void sendUserMessage(User user){
        if (user.getLetterAlarm() && !user.getPhoneNumber().isEmpty()) {
            smsService.sendMessage(user.getPhoneNumber(),"USER");
        }
    }

    @Override
    @Transactional
    public UserResMonthCommentDTO monthComment(UserReqMonthCommentDTO userReqMonthCommentDTO){
        try {
            Long userId = JwtUtil.getUserId();

            // 기존에 다음 달의 한 마디가 존재할 경우 예외 발생
            monthCommentRepository.findCommentByUserIdAndMonth(userId, userReqMonthCommentDTO.getChooseMonth())
                    .ifPresent(monthComment -> { throw new RuntimeException("이미 다음 달의 한 마디가 존재합니다."); });

            MonthComment monthComment = MonthComment.builder()
                    .userId(userId)
                    .commentDate(userReqMonthCommentDTO.getChooseMonth())
                    .commentContent(userReqMonthCommentDTO.getMonthComment())
                    .build();
            monthCommentRepository.save(monthComment);

            return UserResMonthCommentDTO.builder()
                    .chooseMonth(userReqMonthCommentDTO.getChooseMonth())
                    .monthComment(userReqMonthCommentDTO.getMonthComment())
                    .build();
        } catch (Exception e){
            log.error("[UserService] monthComment error"+e);
            throw new RuntimeException(e);
        }
    }


    @Override
    @Transactional
    public UserResMonthCommentUpdateDTO monthCommentUpdate(UserReqMonthCommentUpdateDTO userReqMonthCommentUpdateDTO){
        try{
            Long userId = JwtUtil.getUserId();
            monthCommentRepository.updateCommentByUserIdAndMonth(userId, userReqMonthCommentUpdateDTO.getChooseMonth(), userReqMonthCommentUpdateDTO.getMonthComment());
            MonthComment mc = monthCommentRepository.findCommentByUserIdAndMonth(userId,userReqMonthCommentUpdateDTO.getChooseMonth())
                    .orElseThrow(()->new NoSuchElementException("그 달에 해당하는 한 마디가 없습니다."));
            return UserResMonthCommentUpdateDTO.builder()
                    .chooseMonth(mc.getCommentDate())
                    .monthComment(mc.getCommentContent())
                    .build();
        } catch (Exception e){
            log.error("[UserService] monthCommentUpdate error",e);
            throw new RuntimeException(e);
        }
    }

    /** =========================================================  다연  ========================================================= **/

    //해당하는 월에 유저 아이디로 diary_emotion 조회 -> 감정별로 group by or 불러와서 리스트 또는 hashmap 형태로 가공 (감정(key), 횟수(value))
    @Override
    @Transactional(readOnly = true)
    public UserResStatisticsMonthDTO getMonthStatic(LocalDate month) {
        final Long userId = JwtUtil.getUserId();
        int year = month.getYear();
        int monthValue = month.getMonthValue();

        List<Diary> diaries = diaryRepository.findDiaryEmotionByUserIdAndMonth(userId, year, monthValue);

        // 감정별로 횟수를 세기 위한 Map 생성 및 초기화
        Map<DiaryEmotion, Integer> emotionCountMap = new HashMap<>();

        // 모든 가능한 감정에 대해 기본값 0 설정
        for (DiaryEmotion emotion : DiaryEmotion.values()) {
            emotionCountMap.put(emotion, 0);
        }

        // 일기 데이터를 이용하여 감정별로 횟수를 세기
        for (Diary diary : diaries) {
            DiaryEmotion emotion = diary.getEmotion();
            if (emotion != null && emotionCountMap.containsKey(emotion)) {
                emotionCountMap.put(emotion, emotionCountMap.get(emotion) + 1);
            }
        }

        // LocalDate를 문자열로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        String formattedMonth = month.format(formatter);

        Optional<MonthComment> monthComment = monthCommentRepository.findCommentByUserIdAndMonth(userId, formattedMonth);

        // Map을 UserEmotionStaticDTO 리스트로 변환하고 nums 값으로 내림차순 정렬
        List<UserEmotionStaticDTO> userEmotionStaticDTOList = emotionCountMap.entrySet().stream()
                .map(entry -> new UserEmotionStaticDTO(entry.getKey(), entry.getValue()))
                .sorted((e1, e2) -> e2.getNums().compareTo(e1.getNums())) // nums 값으로 내림차순 정렬
                .collect(Collectors.toList());

        return monthComment.map(mc -> UserResStatisticsMonthDTO.builder()
                        .userEmotionStaticDTOList(userEmotionStaticDTOList)
                        .monthComment(mc.getCommentContent())
                        .commentCheck(true)
                        .build())
                .orElse(UserResStatisticsMonthDTO.builder()
                        .userEmotionStaticDTOList(userEmotionStaticDTOList)
                        .monthComment(null)
                        .commentCheck(false)
                        .build());
    }

    //일기 작성 횟수 조회
    //year parameter로 받아서 -> year에 해당하는 데이터 key,value <month, nums> 형태로 출력
    @Override
    @Transactional(readOnly = true)
    public List<UserDiaryNumsDTO> getDiaryNums(LocalDate year) {

        Long userId = JwtUtil.getUserId();
        int yearValue = year.getYear();

        List<Diary> diaries = diaryRepository.findAllByYear(userId, yearValue);

        Map<Integer, Integer> yearCountMap = new HashMap<>();

        // 1월~12월에 대해 일기 작성 횟수 0으로 초기화
        for (int i = 1; i < 13; i++) {
            yearCountMap.put(i, 0);
        }

        // diary 객체를 이용하여 일기 작성 횟수 세기
        for (Diary diary : diaries) {
            int monthValue = diary.getDate().getMonthValue();
            yearCountMap.put(monthValue, yearCountMap.get(monthValue) + 1);
        }

        // 결과를 정렬하여 반환
        return yearCountMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // 키 값으로 정렬
                .map(entry -> new UserDiaryNumsDTO(entry.getKey() + "월", entry.getValue())) // String 형식으로 변환
                .collect(Collectors.toList());
    }

    // 현재까지 감정보기 - 감정 횟수 조회(해당 년도)
    @Override
    @Transactional(readOnly = true)
    public List<UserEmotionStaticDTO> getEmotionNums(LocalDate month) {
        Long userId = JwtUtil.getUserId();

        List<Diary> diaries = diaryRepository.findDiaryEmotionAllByUserIdAndMonth(userId, month); // 일기를 가져올 때부터 그 month에 해당하는 일기만 가져옴

        log.info("일기들", diaries);

        Map<DiaryEmotion, Integer> emotionCountMap = new HashMap<>();

        for (DiaryEmotion emotion : DiaryEmotion.values()) {
            emotionCountMap.put(emotion, 0);
        }

        for (Diary diary : diaries) {
            DiaryEmotion emotion = diary.getEmotion();
            if (emotion != null && emotionCountMap.containsKey(emotion)) {
                emotionCountMap.put(emotion, emotionCountMap.get(emotion) + 1);
            }
        }

        return emotionCountMap.entrySet().stream()
                .map(entry -> new UserEmotionStaticDTO(entry.getKey(), entry.getValue()))
                .sorted((e1, e2) -> e2.getNums().compareTo(e1.getNums())) // nums 값으로 내림차순 정렬
                .collect(Collectors.toList());
    }

    //프로필 이미지 -> s3 사용
    //프로필 이미지 s3에 저장 -> url setter로 변경
    @Override
    @Transactional
    public ProfileResDetailDTO updateProfile(ProfileReqUpdateDTO requestDTO) {
        Long userId = JwtUtil.getUserId();

        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> new UserNotFoundByUserIdException(ErrorCode.USER_NOT_FOUND)
        );
        Profile profile = profileRepository.findByUserId(userId).orElseThrow(
                () -> new ProfileNotFoundByUserIdException(ErrorCode.PROFILE_NOT_FOUND)
        );
        ProfileImage profileImage = profileImageRepository.findByUserId(userId).orElseThrow(
                () -> new ProfileImageNotFoundByUserIdException(ErrorCode.PROFILE_IMAGE_NOT_FOUND)
        );

        profile.setProfileComment(requestDTO.getProfileComment());
        profileRepository.save(profile);

        user.setAlarm(requestDTO.getAlarm());
        user.setAlarmTime(requestDTO.getAlarmTime());
        user.setPhoneNumber(requestDTO.getPhoneNumber());
        user.setNickname(requestDTO.getNickname());
        user.setGender(requestDTO.getGender());
        user.setBirthday(requestDTO.getBirthday());
        userRepository.save(user);

        String profileImageUrl = requestDTO.getProfileImageUrl();
        if (profileImageUrl != null) {
            profileImage.setProfileImgURL(profileImageUrl);
        }

        return createUserResProfileDTO(user, profile, profileImage);
    }

    private ProfileResDetailDTO createUserResProfileDTO(User user, Profile profile, ProfileImage profileImage) {
        return ProfileResDetailDTO.builder()
                .url(profileImage.getProfileImgURL())
                .profileComment(profile.getProfileComment())
                .nickname(user.getNickname())
                .alarm(user.getAlarm())
                .alarmTime(user.getAlarmTime())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .birthday(user.getBirthday())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResDetailDTO getUserProfile() {
        Long userId = JwtUtil.getUserId();

        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> new UserNotFoundByUserIdException(ErrorCode.USER_NOT_FOUND)
        );
        Profile profile = profileRepository.findByUserId(userId).orElseThrow(
                () -> new ProfileNotFoundByUserIdException(ErrorCode.PROFILE_NOT_FOUND)
        );
        ProfileImage profileImage = profileImageRepository.findByUserId(userId).orElseThrow(
                () -> new ProfileImageNotFoundByUserIdException(ErrorCode.PROFILE_IMAGE_NOT_FOUND)
        );

        return createUserResProfileDTO(user, profile, profileImage);
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 1 * ?") // 매월 1일 자정에 자동으로 실행
    public void changeDiaryNums(){
        List<User> users = userRepository.findAll();
        for(User user : users){
            user.setUserCurDiaryNums(0);// 새로운 달의 일기 개수를 위해 userCurDiaryNums 초기화
        }
    }

    @Override
    @Transactional
    public void changeCount(Long userId, boolean increment) {
        User user = getUserById(userId);
        if (!increment) {
            user.plusUserNumCount();
        } else {
            user.minusUserNumCount();
        }
    }

    @Override
    public UserResCheckTodayDiaryDTO checkTodayDiary() {
        Long userId = JwtUtil.getUserId();
        return UserResCheckTodayDiaryDTO.builder()
                .userId(userId)
                .checkTodayDairy(getUserById(userId).getCheckTodayDairy())
                .build();
    }

    @Override
    public void setUserCheckTodayDairy(Long userId, Boolean check) {
        User findUser = getUserById(userId);
        findUser.setCheckTodayDiary(check);
    }

    /** 테스트를 위한 임시 자체 로그인 **/
    @Override
    public UserResLoginDTO login(UserReqLoginDTO userReqLoginDTO) {
        return UserMapper.toUserResLoginDTO(getUserById(userReqLoginDTO.getUserId()));
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(()->new UserNotFoundByUserIdException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public User getUserByKakaoId(Long kakaoId) {
        return userRepository.findByKakaoId(kakaoId)
                .orElseThrow(()->new UserNotFoundByUserIdException(ErrorCode.USER_NOT_FOUND));
    }
}