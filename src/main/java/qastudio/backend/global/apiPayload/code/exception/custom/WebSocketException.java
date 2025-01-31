package qastudio.backend.global.apiPayload.code.exception.custom;

import org.springframework.http.HttpStatus;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

public class WebSocketException extends RuntimeException {
    private final ErrorStatus errorStatus;

    public WebSocketException(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }

    public HttpStatus getHttpStatus() {
        return errorStatus.getHttpStatus();
    }

    public String getErrorCode() {
        return errorStatus.getCode();
    }

    public String getErrorMessage() {
        return errorStatus.getMessage();
    }
}
