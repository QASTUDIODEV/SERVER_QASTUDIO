package qastudio.backend.domain.test.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TestRequest {
    private String testName;
    private int attainment;
    private String state;
    private double time;
    private Long userId;
    private Long projectId;
    private Long pageId;
    private String scenarioRecord;
    private int totalActionCount;
    private int executionActionCount;
}
