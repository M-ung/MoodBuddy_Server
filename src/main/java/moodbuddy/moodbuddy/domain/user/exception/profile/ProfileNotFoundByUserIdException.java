package moodbuddy.moodbuddy.domain.user.exception.profile;

import lombok.Getter;
import moodbuddy.moodbuddy.global.error.ErrorCode;
import moodbuddy.moodbuddy.global.error.MoodBuddyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
public class ProfileNotFoundByUserIdException extends MoodBuddyException {
    public ProfileNotFoundByUserIdException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
