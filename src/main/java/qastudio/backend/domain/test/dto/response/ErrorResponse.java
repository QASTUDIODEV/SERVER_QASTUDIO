package qastudio.backend.domain.test.dto.response;

import lombok.*;

public class ErrorResponse {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Error {
        private Long testId;
        private String testName;
        private String errorImage;
        private String errorCode;
        private String errorMessage;
    }
}
