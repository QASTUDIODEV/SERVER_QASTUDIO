package qastudio.backend.domain.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

public class PageRequest {

    @Getter
    public static class createPage {
        // 페이지 이름
        @NotBlank(message = "pageName 은 필수 입력 값입니다.")
        private String pageName;
        // 페이지 설명
        @NotBlank(message = "pageDescription 은 필수 입력 값입니다.")
        private String pageDescription;
        // 페이지 경로
        @NotBlank(message = "path 은 필수 입력 값입니다.")
        private String path;
        // 페이지 접근 가능 권한
        @NotNull(message = "characterIdList 은 필수 입력 값입니다.")
        private List<Long> characterIdList;
        // 시나리오
        @NotNull(message = "scenarioList 은 필수 입력 값입니다.")
        private List<String> scenarioList;
    }

}
