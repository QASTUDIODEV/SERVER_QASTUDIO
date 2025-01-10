package qastudio.backend.domain.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;

public class ProjectRequest {

    @Getter
    public static class UpdateIntroduce {
        // 프로젝트 소개
        @NotBlank(message = "Introduce 필드는 공백일 수 없습니다.")
        @Size(max = 500, message = "Introduce 필드는 500자 이내로 작성해야 합니다.")
        private String introduce;
    }

    @Getter
    public static class CreateProject {
        // 프로젝트 생성
        private String projectImage;
        @NotBlank(message = "projectName 은 필수 입력 값입니다.")
        private String projectName;
        private String projectUrl;
        private List<String> memberEmail;
    }
}
