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
    INVALID_USER_ID_FORMAT(HttpStatus.BAD_REQUEST, "AUTH400", "유효하지 않은 사용자 ID 형식입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "AUTH401", "비밀번호가 잘못되었습니다."),
    MISSING_AUTHORITY(HttpStatus.FORBIDDEN, "AUTH403", "권한 정보가 없는 토큰입니다. 기본 권한이 필요합니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH404", "존재하지 않는 사용자입니다."),
    ALREADY_EXIST_EMAIL(HttpStatus.CONFLICT, "AUTH409", "이메일 인증 코드 전송을 실패했습니다."),

    // 이메일 관련 에러
    EMAIL_VERIFICATION_SEND_FAILED(HttpStatus.BAD_REQUEST, "EMAIL400", "이메일 인증 코드 전송을 실패했습니다."),

    // 토큰 관련 에러
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN401", "토큰이 유효하지 않습니다."),
    TOKEN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "TOKEN500", "토큰 처리 중 에러가 발생했습니다."),

    // 프로젝트 관련 에러
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT404", "존재하지 않는 프로젝트입니다."),

    // 역할 관련 에러
    CHARACTER_NOT_FOUND(HttpStatus.NOT_FOUND, "CHARACTER404", "존재하지 않는 역할입니다."),

    // 페이지 관련 에러
    PAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "PAGE404", "존재하지 않는 페이지입니다."),

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
