package qastudio.backend.global.apiPayload.code.exception.custom;

import qastudio.backend.global.apiPayload.code.exception.GeneralException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

public class TeamMemberException extends GeneralException {
    public TeamMemberException(final ErrorStatus errorStatus) {
      super(errorStatus);
    }
}