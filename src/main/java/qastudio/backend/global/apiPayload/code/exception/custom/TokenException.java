package qastudio.backend.global.apiPayload.code.exception.custom;

import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

public class TokenException extends CustomException {
    public TokenException(ErrorStatus errorStatus, String message) {
        super(errorStatus, message);
    }

    public TokenException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
