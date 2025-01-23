package qastudio.backend.domain.scenario.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class ActionResponse {
    private String actionDescription;
    private Integer step;
    private String actionType;
    private Map<String, Object> locator;
    private Map<String, Object> action;
}
