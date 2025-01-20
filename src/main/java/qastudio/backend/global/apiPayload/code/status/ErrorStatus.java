package qastudio.backend.global.apiPayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import qastudio.backend.global.apiPayload.code.BaseErrorCode;
import qastudio.backend.global.apiPayload.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 기존 에러들
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // For test
    TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "이거는 테스트"),

    // Auth 관련 에러
    INVALID_USER_ID_FORMAT(HttpStatus.BAD_REQUEST, "AUTH400", "Invalid user ID format."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "AUTH401", "Incorrect password."),
    MISSING_AUTHORITY(HttpStatus.FORBIDDEN, "AUTH403", "The token lacks authority information. Basic authority is required."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH404", "User not found."),
    ALREADY_EXIST_EMAIL(HttpStatus.CONFLICT, "AUTH409", "Email already registered."),
    PASSWORD_ALREADY_USED(HttpStatus.CONFLICT, "AUTH410", "Password already in use."),

    // 이메일 관련 에러
    EMAIL_VERIFICATION_SEND_FAILED(HttpStatus.BAD_REQUEST, "EMAIL400", "이메일 인증 코드 전송을 실패했습니다."),

    // 토큰 관련 에러
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN401", "토큰이 유효하지 않습니다."),
    TOKEN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "TOKEN500", "토큰 처리 중 에러가 발생했습니다."),

    // 프로젝트 관련 에러
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT404", "존재하지 않는 프로젝트입니다."),

    // 역할 관련 에러
    CHARACTER_NOT_FOUND(HttpStatus.NOT_FOUND, "CHARACTER404", "존재하지 않는 역할입니다."),
    CHARACTER_NOT_IN_PROJECT(HttpStatus.BAD_REQUEST, "CHARACTER400", "프로젝트에 속하지 않는 역할입니다."),

    // 팀원 관련 에러
    UNMATCHED_USER(HttpStatus.BAD_REQUEST, "MEMBER400", "userId와 이메일 정보가 일치하지 않습니다."),
    ALREADY_REGISTERED_MEMBER(HttpStatus.CONFLICT, "MEMBER409", "이미 프로젝트에 추가된 유저입니다."),

    // 페이지 관련 에러
    PAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "PAGE404", "존재하지 않는 페이지입니다."),

    // 테스트 관련 에러
    TEST_NOT_FOUND(HttpStatus.NOT_FOUND, "TEST404", "존재하지 않는 테스트입니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
