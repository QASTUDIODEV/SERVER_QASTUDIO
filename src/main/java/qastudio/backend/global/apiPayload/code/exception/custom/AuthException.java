package qastudio.backend.global.apiPayload.code.exception.custom;

import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

public class AuthException extends CustomException {
    public AuthException(ErrorStatus errorStatus, String message) {
        super(errorStatus, message);
    }

    public AuthException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
