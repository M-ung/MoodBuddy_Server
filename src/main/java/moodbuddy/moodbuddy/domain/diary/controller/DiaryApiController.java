package moodbuddy.moodbuddy.domain.diary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moodbuddy.moodbuddy.domain.diary.dto.request.*;
import moodbuddy.moodbuddy.domain.diary.dto.response.*;
import moodbuddy.moodbuddy.domain.diary.service.DiaryServiceImpl;
import moodbuddy.moodbuddy.global.common.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/member/diary")
@Tag(name = "Diary", description = "일기 관련 API")
@RequiredArgsConstructor
@Slf4j
public class DiaryApiController {
    private final DiaryServiceImpl diaryService;
    /** 구현 완료 **/
    @PostMapping("/save")
    @Operation(summary = "일기 작성", description = "새로운 일기를 작성합니다.")
    public ResponseEntity<?> save(@Parameter(description = "일기 정보를 담고 있는 DTO")
                                      @ModelAttribute DiaryReqSaveDTO diaryReqSaveDTO) throws IOException {
        log.info("[DiaryApiController] save");
        DiaryResDetailDTO result = diaryService.save(diaryReqSaveDTO);
        return ResponseEntity.ok().body(ApiResponse.SUCCESS(HttpStatus.CREATED.value(), "[SUCCESS] DiaryApiController save", result));
    }
    /** 구현 완료 **/
    @PatchMapping("/update")
    @Operation(summary = "일기 수정", description = "기존 일기를 수정합니다.")
    public ResponseEntity<?> update(@Parameter(description = "수정된 일기 정보를 담고 있는 DTO")
                                        @ModelAttribute DiaryReqUpdateDTO diaryReqUpdateDTO) throws IOException {
        log.info("[DiaryApiController] update");
        DiaryResDetailDTO result = diaryService.update(diaryReqUpdateDTO);
        return ResponseEntity.ok().body(ApiResponse.SUCCESS(HttpStatus.CREATED.value(), "[SUCCESS] DiaryApiController update", result));
    }
    /** 구현 완료 **/
    @DeleteMapping("/delete/{diaryId}")
    @Operation(summary = "일기 삭제", description = "기존 일기를 삭제합니다.")
    public ResponseEntity<?> delete(@Parameter(description = "일기 고유 식별자 diaryId")
                                        @PathVariable("diaryId") Long diaryId) {
        log.info("[DiaryApiController] delete");
        diaryService.delete(diaryId);
        return ResponseEntity.ok().body(ApiResponse.SUCCESS(HttpStatus.CREATED.value(), "[SUCCESS] DiaryApiController delete"));
    }
    /** 구현 완료 **/
    @PostMapping("/draftSave")
    @Operation(summary = "일기 임시 저장", description = "일기를 임시 저장합니다.")
    public ResponseEntity<?> draftSave(@Parameter(description = "임시 저장 일기 정보를 담고 있는 DTO")
                                           @ModelAttribute DiaryReqSaveDTO diaryReqSaveDTO) throws IOException {
        log.info("[DiaryApiController] draftSave");
        DiaryResDetailDTO result = diaryService.draftSave(diaryReqSaveDTO);
        return ResponseEntity.ok().body(ApiResponse.SUCCESS(HttpStatus.CREATED.value(), "[SUCCESS] DiaryApiController draftSave", result));
    }
    /** 구현 완료 **/
    @GetMapping("/draftFindAll")
    @Operation(summary = "임시 저장 일기 목록 조회", description = "임시 저장 일기를 모두 조회합니다.")
    public ResponseEntity<?> draftFindAll() {
        log.info("[DiaryApiController] draftFindAll");
        DiaryResDraftFindAllDTO result = diaryService.draftFindAll();
        return ResponseEntity.ok().body(ApiResponse.SUCCESS(HttpStatus.CREATED.value(), "[SUCCESS] DiaryApiController draftFindAll", result));
    }
    /** 구현 완료 **/
    @DeleteMapping("/draftSelectDelete")
    @Operation(summary = "임시 저장 일기 선택 삭제", description = "임시 저장 일기를 선택해서 삭제합니다.")
    public ResponseEntity<?> draftSelectDelete(@Parameter(description = "삭제할 임시 저장 일기 고유 식별자를 담고 있는 DTO")
                                                   @RequestBody DiaryReqDraftSelectDeleteDTO diaryReqDraftSelectDeleteDTO) {
        log.info("[DiaryApiController] draftSelectDelete");
        diaryService.draftSelectDelete(diaryReqDraftSelectDeleteDTO);
        return ResponseEntity.ok().body(ApiResponse.SUCCESS(HttpStatus.CREATED.value(), "[SUCCESS] DiaryApiController draftSelectDelete"));
    }
    /** 구현 완료 **/
    @GetMapping("/findOne/{diaryId}")
    @Operation(summary = "일기 하나 조회", description = "일기 하나를 조회합니다.")
    public ResponseEntity<?> findOneByDiaryId(@Parameter(description = "일기 고유 식별자 diaryId")
                                                  @PathVariable("diaryId") Long diaryId) {
        log.info("[DiaryApiController] findOne");
        DiaryResDetailDTO result = diaryService.findOneByDiaryId(diaryId);
        return ResponseEntity.ok().body(ApiResponse.SUCCESS(HttpStatus.CREATED.value(), "[SUCCESS] DiaryApiController findOne", result));
    }
    /** 구현 완료 **/
    @GetMapping("/findAllPageable")
    @Operation(summary = "일기 전체 조회", description = "일기를 모두 조회합니다.")
    public ResponseEntity<?> findAllWithPageable(Pageable pageable) {
        log.info("[DiaryApiController] findAllPageable");
        Page<DiaryResDetailDTO> result = diaryService.findAllWithPageable(pageable);
        return ResponseEntity.ok().body(ApiResponse.SUCCESS(HttpStatus.CREATED.value(), "[SUCCESS] DiaryApiController findAllPageable", result));
    }
    /** 구현 완료 **/
    @GetMapping("/findAllByEmotionWithPageable")
    @Operation(summary = "일기 감정으로 일기 전체 조회", description = "감정이 똑같은 일기를 모두 조회합니다.")
    public ResponseEntity<?> findAllByEmotionWithPageable(@Parameter(description = "감정 데이터를 담고 있는 DTO")
                                                              @RequestBody DiaryReqEmotionDTO diaryReqEmotionDTO, Pageable pageable) {
        log.info("[DiaryApiController] findAllByEmotionWithPageable");
        Page<DiaryResDetailDTO> result = diaryService.findAllByEmotionWithPageable(diaryReqEmotionDTO, pageable);
        return ResponseEntity.ok().body(ApiResponse.SUCCESS(HttpStatus.CREATED.value(), "[SUCCESS] DiaryApiController findAllByEmotionWithPageable", result));
    }
    /** 구현 완료(키워드 제외) **/
    @GetMapping("/findAllByFilter")
    @Operation(summary = "일기 필터링으로 전체 조회", description = "여러 필터링을 선택하여 일기를 모두 조회합니다.")
    public ResponseEntity<?> findAllByFilter(@Parameter(description = "필터링 데이터를 담고 있는 DTO")
                                                 @RequestBody DiaryReqFilterDTO diaryReqFilterDTO, Pageable pageable) {
        log.info("[DiaryApiController] findAllByFilter");
        Page<DiaryResDetailDTO> result = diaryService.findAllByFilter(diaryReqFilterDTO, pageable);
        return ResponseEntity.ok().body(ApiResponse.SUCCESS(HttpStatus.CREATED.value(), "[SUCCESS] DiaryApiController findAllByFilter", result));
    }
    /** 키워드 검색 기능 구현 **/
}
