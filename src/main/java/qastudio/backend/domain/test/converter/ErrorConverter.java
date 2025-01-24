package qastudio.backend.domain.test.converter;

import qastudio.backend.domain.test.dto.response.ErrorResponse;
import qastudio.backend.domain.test.entity.Error;

public class ErrorConverter {

    public static ErrorResponse.ErrorDetail toError(Error error) {
        return ErrorResponse.ErrorDetail.builder()
                .testId(error.getTest().getId())
                .testName(error.getTest().getTestName())
                .errorImage(error.getErrorImage())
                .errorCode(error.getErrorCode())
                .errorMessage(error.getErrorMessage())
                .scenarioRecord(error.getTest().getScenarioRecord())
                .build();
    }
}
