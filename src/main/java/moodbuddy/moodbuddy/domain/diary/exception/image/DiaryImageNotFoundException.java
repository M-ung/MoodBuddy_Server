package moodbuddy.moodbuddy.domain.diary.exception.image;

import lombok.Getter;
import moodbuddy.moodbuddy.global.error.ErrorCode;
import moodbuddy.moodbuddy.global.error.MoodBuddyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
public class DiaryImageNotFoundException extends MoodBuddyException {
    public DiaryImageNotFoundException(final ErrorCode errorCode) {
        super(errorCode);
    }
}