package qastudio.backend.global.apiPayload.code.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import qastudio.backend.global.apiPayload.code.BaseErrorCode;
import qastudio.backend.global.apiPayload.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private BaseErrorCode code;
    private String customMessage;

    public GeneralException(BaseErrorCode code) {
        this.code = code;
        this.customMessage = code.getReason().getMessage();
    }

    public ErrorReasonDTO getErrorReason() {
        return this.code.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus() {
        return this.code.getReasonHttpStatus();
    }
}


