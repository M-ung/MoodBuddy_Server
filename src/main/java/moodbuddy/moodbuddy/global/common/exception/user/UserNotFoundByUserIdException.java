package moodbuddy.moodbuddy.global.common.exception.user;

import lombok.Getter;
import moodbuddy.moodbuddy.global.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
public class UserNotFoundByUserIdException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Long userId;
    public UserNotFoundByUserIdException(final Long userId, final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.userId = userId;
    }
}