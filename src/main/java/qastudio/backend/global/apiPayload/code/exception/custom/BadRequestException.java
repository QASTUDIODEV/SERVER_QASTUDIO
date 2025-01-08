package qastudio.backend.global.apiPayload.code.exception.custom;

import lombok.Getter;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

@Getter
public class BadRequestException extends RuntimeException {

    private final String code;

    public BadRequestException(final ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.code = errorStatus.getCode();
    }
}
