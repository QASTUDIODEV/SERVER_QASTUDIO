package qastudio.backend.global.apiPayload.code.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import qastudio.backend.global.apiPayload.code.BaseErrorCode;
import qastudio.backend.global.apiPayload.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private BaseErrorCode code;

    public ErrorReasonDTO getErrorReason() {
        return this.code.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus(){
        return this.code.getReasonHttpStatus();
    }
}
