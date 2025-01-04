package qastudio.backend.global.apiPayload.code.exception.handler;

import qastudio.backend.global.apiPayload.code.BaseErrorCode;
import qastudio.backend.global.apiPayload.code.exception.GeneralException;

public class TempHandler extends GeneralException {

    public TempHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
