package qastudio.backend.domain.selenium.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SeleniumExecutionResponse {
    private String status;
    private List<String> logs;
    private String html;
    private String css;
}