package qastudio.backend.domain.selenium.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SeleniumExecutionResponse {
    private String status;
    private List<String> logs;
    private String html;
    private String css;
    private Long testId;

    public SeleniumExecutionResponse(String status, List<String> logs) {
        this.status = status;
        this.logs = logs;
        this.html = null;
        this.css = null;
    }

    public SeleniumExecutionResponse(String status, List<String> logs, String html, String css) {
        this.status = status;
        this.logs = logs;
        this.html = html;
        this.css = css;
        this.testId = null;
    }
}
