package qastudio.backend.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public class UserRequest {
    @Getter
    public static class UpdateUserInfo {
        // 닉네임
        @NotBlank(message = "nickname 필드는 공백일 수 없습니다.")
        private String nickname;
        // 프로필 이미지
        private String profileImage;
        // 배너 이미지
        private String bannerImage;
    }
}
