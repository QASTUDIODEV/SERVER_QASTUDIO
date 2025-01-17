package qastudio.backend.global.apiPayload.code.exception.custom;

import qastudio.backend.global.apiPayload.code.exception.GeneralException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

public class CharacterException extends GeneralException {
  public CharacterException(final ErrorStatus errorStatus) {
    super(errorStatus);
  }
}
