package qastudio.backend.global.apiPayload.code.exception.custom;

import qastudio.backend.global.apiPayload.code.exception.GeneralException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

public class BadRequestException extends GeneralException {
    public BadRequestException(final ErrorStatus errorStatus) {
        super(errorStatus);
    }
}