package qastudio.backend.domain.scenario.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActionRequest {

    @NotNull
    private String actionDescription;

    @NotNull
    private Integer step;

    @NotNull
    private String actionType;

    @NotNull
    private Map<String, Object> locator;

    @NotNull
    private Map<String, Object> action;
}
