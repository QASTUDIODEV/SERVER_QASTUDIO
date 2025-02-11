package qastudio.backend.domain.selenium.dto.response;

import com.fasterxml.jackson.annotation.JsonRawValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageResultResponse {

    @Schema(description = "실행 상태 (SUCCESS / FAILURE)", example = "SUCCESS")
    private String status;

    @Schema(description = "실행 단계", example = "BEFORE_ACTION")
    private String phase;

    @Schema(description = "실행 로그", example = "[\"Scenario 실행 시작\", \"URL 접근: https://www.wikipedia.org\"]")
    private List<String> executionLogs;

    @JsonRawValue  // JSON 내부 개행을 유지하도록 처리
    @Schema(description = "추출된 HTML", example = "<html>...</html>")
    private String html;

    @JsonRawValue  // JSON 내부 개행을 유지하도록 처리
    @Schema(description = "추출된 CSS", example = "body { background: red; }")
    private String css;

    private Long actionId;
}
