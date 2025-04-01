package moodbuddy.moodbuddy.domain.user.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class UserResLoginDTO {
    private Long userId;
    private String accessToken;
}