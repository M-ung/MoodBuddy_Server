package moodbuddy.moodbuddy.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /** User **/
    USER_NOT_FOUND(400, "USER-ERR-400", "userId에 해당하는 회원을 찾을 수 없습니다."),

    /** Profile **/
    PROFILE_NOT_FOUND(400, "PROFILE-ERR-400", "userId에 해당하는 프로필을 찾을 수 없습니다."),
    PROFILE_IMAGE_NOT_FOUND(400, "PROFILE-IMAGE-ERR-400", "userId에 해당하는 프로필 이미지를 찾을 수 없습니다."),

    /** Diary **/
    DIARY_CONCURRENT_UPDATE(400, "DIARY-ERR-400", "동시에 일기를 수정할 수 없습니다."),
    DIARY_CONCURRENT_DELETE(400, "DIARY-ERR-400", "동시에 일기를 삭제할 수 없습니다."),
    DIARY_NOT_FOUND(400, "DIARY-ERR-400", "일기를 찾을 수 없습니다."),
    DIARY_NO_ACCESS(403, "DIARY-ERR-403", "접근할 수 없는 일기입니다."),
    DIARY_TODAY_EXISTING(409, "DIARY-ERR-409", "오늘 이미 일기를 작성했습니다."),

    /** DraftDiary **/
    DRAFT_DIARY_NOT_FOUND(400, "DRAFT-DIARY-ERR-400", "임시 저장 일기를 찾을 수 없습니다."),
    DRAFT_DIARY_NO_ACCESS(403, "DRAFT-DIARY-ERR-403", "접근할 수 없는 임시 저장 일기입니다."),
    DRAFT_DIARY_CONCURRENT_UPDATE(400, "DRAFT-DIARY-ERR-400", "동시에 임시 저장 일기를 수정할 수 없습니다."),
    DRAFT_DIARY_CONCURRENT_DELETE(400, "DRAFT-DIARY-ERR-400", "동시에 임시 저장 일기를 삭제할 수 없습니다."),


    /** Letter **/
    LETTER_NOT_FOUND_BY_ID(400, "LETTER-ERR-400","letterId에 해당하는 편지를 찾을 수 없습니다."),
    LETTER_NOT_FOUND_BY_ID_AND_USER_ID(400, "LETTER-ERR-400","letterId와 userId에 해당하는 회원을 찾을 수 없습니다."),
    LETTER_INVALID_NUMS(400, "LETTER-ERR-400","userId에 해당하는 회원의 편지지가 없습니다."),

    /** Gpt **/
    GPT_PARSE_ERROR(422, "GPT-ERR-422", "Json 형태의 GPT 응답을 파싱하는 중 오류가 발생했습니다."),
    GPT_BAD_REQUEST(400, "GPT-ERR-423", "OpenAI 호출 중 에러가 발생했습니다."),

    /** QuddyTI **/
    QUDDYTI_NOT_FOUND(400, "QUDDYTI-ERR-400", "쿼디티아이를 찾을 수 없습니다."),
    QUDDYTI_INVALIDATE(400, "QUDDYTI-ERR-400", "쿼디티아이를 결정 중 에러가 발생했습니다."),

    /** 5XX **/
    INTERNAL_SERVER_ERROR(500,"COMMON-ERR-500","INTER SERVER ERROR");

    private int status;
    private String errorCode;
    private String message;
}

