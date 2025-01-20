package qastudio.backend.domain.scenario.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Element {

    private String name;
    private String type;
    private Locator locator;
    private Action action;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Locator {
        private String strategy;
        private String value;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Action {
        private String type;
        private String value;
    }
}
