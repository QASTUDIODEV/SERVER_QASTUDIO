package qastudio.backend.global.apiPayload.code.exception.custom;

import qastudio.backend.global.apiPayload.code.exception.GeneralException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

public class TokenException extends GeneralException {
    public TokenException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
