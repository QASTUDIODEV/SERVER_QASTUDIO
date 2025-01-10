package qastudio.backend.domain.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

public class ProjectRequest {

    @Getter
    public static class UpdateIntroduce {
        // 프로젝트 소개
        @NotBlank(message = "Introduce 필드는 공백일 수 없습니다.")
        @Size(max = 500, message = "Introduce 필드는 500자 이내로 작성해야 합니다.")
        private String introduce;
    }
}
