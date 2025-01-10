package qastudio.backend.domain.project.dto.request;

import jakarta.validation.constraints.NotBlank;
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
        @NotBlank(message = "accessPage 필드는 필수 입력 값입니다.")
        private List<String> accessPage;
    }
}
