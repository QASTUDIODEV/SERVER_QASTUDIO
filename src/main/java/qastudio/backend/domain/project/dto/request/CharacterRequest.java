package qastudio.backend.domain.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Getter;

public class CharacterRequest {

    @Getter
    public static class CreateCharacter {
        // 역할 생성
        @NotBlank(message = "characterName 필드는 필수 입력 값입니다.")
        private String characterName;
        @NotBlank(message = "characterDescription 필드는 필수 입력 값입니다.")
        private String characterDescription;
        @NotEmpty(message = "accessPage 필드는 필수 입력 값입니다.")
        private List<@NotBlank(message = "accessPage의 각 값은 빈 문자열일 수 없습니다.") String> accessPage;
    }

    @Getter
    public static class UpdateCharacter {
        // 역할 수정
        @NotBlank(message = "characterName 필드는 필수 입력 값입니다.")
        private String characterName;
        @NotBlank(message = "characterDescription 필드는 필수 입력 값입니다.")
        private String characterDescription;
        @NotEmpty(message = "accessPage 필드는 필수 입력 값입니다.")
        private List<@NotBlank(message = "accessPage의 각 값은 빈 문자열일 수 없습니다.") String> accessPage;
    }
}
