package moodbuddy.moodbuddy.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moodbuddy.moodbuddy.domain.profile.dto.request.ProfileReqUpdateDTO;
import moodbuddy.moodbuddy.domain.user.dto.request.*;
import moodbuddy.moodbuddy.domain.user.service.UserService;
import moodbuddy.moodbuddy.global.util.JwtUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/member")
public class UserApiController {
    private final UserService userService;

    @GetMapping("/main")
    @Operation(summary = "메인 화면", description = "메인 화면으로 이동합니다.")
    public ResponseEntity<?> mainPage(){
        return ResponseEntity.ok(userService.mainPage());
    }

    @PostMapping("/main/month")
    @Operation(summary = "캘린더 달 이동", description = "캘린더의 달을 이동시킵니다.")
    public ResponseEntity<?> monthlyCalendar(
            @Parameter(description = "캘린더에서 이동할 년, 월을 담고 있는 DTO")
            @RequestBody UserReqCalendarMonthDTO calendarMonthDTO) {
        return ResponseEntity.ok(userService.monthlyCalendar(calendarMonthDTO));
    }

    @PostMapping("/main/summary")
    @Operation(summary = "일기 한 줄 요약", description = "사용자가 선택한 날짜의 일기를 한 줄 요약한 결과를 보여줍니다.")
    public ResponseEntity<?> summary(
            @Parameter(description = "사용자가 선택한 날짜를 담고 있는 DTO")
            @RequestBody UserReqCalendarSummaryDTO calendarSummaryDTO) {
        return ResponseEntity.ok(userService.summary(calendarSummaryDTO));
    }

    //월별 통계 보기
    @GetMapping("/main/month-static")
    @Operation(summary = "월별 통계 보기", description = "사용자가 선택한 월의 통계(감정 리스트, 짧은 한 마디) 를 보여줍니다.")
    @Parameters(@Parameter(name="month", description = "YYYY-MM-DD 형식으로 입력하세요"))
    public ResponseEntity<?> getMonthStatic(
            @RequestParam("month") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {
        return ResponseEntity.ok(userService.getMonthStatic(month));
    }

    // 다음 달 나에게 짧은 한 마디
    @PostMapping("/main/month-comment")
    @Operation(summary = "다음 달 나에게 짧은 한 마디")
    public ResponseEntity<?> monthComment(
            @Parameter(description = "월별 통계의 달과 한 마디를 받아오는 DTO")
            @RequestBody UserReqMonthCommentDTO userReqMonthCommentDTO) {
        return ResponseEntity.ok(userService.monthComment(userReqMonthCommentDTO));
    }

    // 다음 달 나에게 짧은 한 마디 수정
    @PostMapping("/main/month-comment-update")
    @Operation(summary = "다음 달 나에게 짧은 한 마디 수정")
    public ResponseEntity<?> monthCommentUpdate(
            @Parameter(description = "월별 통계의 달과 수정할 한 마디를 받아오는 DTO")
            @RequestBody UserReqMonthCommentUpdateDTO userReqMonthCommentUpdateDTO) {
        return ResponseEntity.ok(userService.monthCommentUpdate(userReqMonthCommentUpdateDTO));
    }


    //내 활동 _ 일기 횟수 조회 , 년 + 해당하는 월
    @GetMapping("/main/diary-nums")
    @Operation(summary = "현재까지 작성한 일기 횟수", description = "해당 년도의 월별로 작성한 일기 횟수를 보여줍니다.")
    @Parameters({
            @Parameter(name="year", description = "YYYY-MM-DD 형식으로 입력하세요"),})
    public ResponseEntity<?> getDiaryNums(
            @RequestParam("year") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate year) {
        return ResponseEntity.ok(userService.getDiaryNums(year));
    }


    //감정 횟수 조회
    @GetMapping("/main/emotion-nums")
    @Operation(summary = "현재까지 감정보기", description = "현재까지 감정보기를 위한 각 감정에 따른 감정 횟수를 보여줍니다")
    @Parameters({
            @Parameter(name="month", description = "YYYY-MM 형식으로 입력하세요"),
    })
    public ResponseEntity<?> getEmotionNums(
            @RequestParam("month") @DateTimeFormat(pattern = "yyyy-MM") LocalDate month) {
        return ResponseEntity.ok(userService.getEmotionNums(month));
    }

    //프로필 조회
    @GetMapping("/main/profile")
    @Operation(summary = "프로필 조회")
    public ResponseEntity<?> getProfile() {
        return ResponseEntity.ok(userService.getUserProfile());
    }

    //프로필 수정
    @PostMapping ("/main/profile-edit")
    @Operation(summary = "프로필 수정", description = "alarmTime(str) -> HH:mm 형식,birthday(str) -> YYYY-mm-dd 형식 ")
    public ResponseEntity<?> updateProfile(
            @ModelAttribute ProfileReqUpdateDTO updateDto) {
        Long kakaoId = JwtUtil.getUserId();
        userService.scheduleUserMessage(kakaoId);
        return ResponseEntity.ok(userService.updateProfile(updateDto));
    }

    /** 테스트를 위한 임시 자체 로그인 **/
    @PostMapping("/test/login")
    @Operation(summary = "자체 로그인")
    public ResponseEntity<?> login(
            @Parameter(description = "자체 로그인 회원 정보를 받아오는 DTO")
            @RequestBody UserReqLoginDTO userReqLoginDTO) {
        return ResponseEntity.ok().body(userService.login(userReqLoginDTO));
    }

    @GetMapping("/checkTodayDiary")
    @Operation(summary = "오늘 일기 작성 가능 여부 확인")
    public ResponseEntity<?> checkTodayDiary(){
        return ResponseEntity.ok().body(userService.checkTodayDiary());
    }
}
