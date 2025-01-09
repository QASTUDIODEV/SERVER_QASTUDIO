package qastudio.backend.global.apiPayload.code.exception.custom;

import qastudio.backend.global.apiPayload.code.exception.GeneralException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

public class AuthException extends GeneralException {

    public AuthException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
