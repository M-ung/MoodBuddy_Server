package moodbuddy.moodbuddy.domain.user.mapper;
import moodbuddy.moodbuddy.domain.user.dto.response.UserResLoginDTO;
import moodbuddy.moodbuddy.domain.user.domain.User;
import moodbuddy.moodbuddy.global.util.JwtUtil;
import org.modelmapper.ModelMapper;

public class UserMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static UserResLoginDTO toUserResLoginDTO(User user) {
        Long userId = user.getId();
        return UserResLoginDTO.builder()
                .userId(userId)
                .accessToken(JwtUtil.createAccessToken(userId))
                .refreshToken(JwtUtil.createRefreshToken(userId))
                .build();
    }
}
