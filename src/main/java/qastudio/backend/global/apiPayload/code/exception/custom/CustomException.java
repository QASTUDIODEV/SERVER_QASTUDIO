package qastudio.backend.global.apiPayload.code.exception.custom;

import lombok.AllArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {
    private ErrorStatus errorStatus;
    private String message;

    public CustomException(ErrorStatus errorStatus) {
        this.errorStatus = errorStatus;
        this.message = errorStatus.getMessage();
    }
}
