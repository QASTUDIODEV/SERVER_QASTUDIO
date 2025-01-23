package qastudio.backend.domain.selenium.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SeleniumExecutionResponse {
    private String status;
    private List<String> logs;
}
