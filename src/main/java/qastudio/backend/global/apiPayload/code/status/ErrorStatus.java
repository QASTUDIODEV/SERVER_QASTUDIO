package qastudio.backend.global.apiPayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import qastudio.backend.global.apiPayload.code.BaseErrorCode;
import qastudio.backend.global.apiPayload.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // Existing errors
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "Server error, please contact the administrator."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","Invalid request."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","Authentication required."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "Forbidden request."),

    // For test
    TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "This is a test."),

    // Auth-related errors
    INVALID_USER_ID_FORMAT(HttpStatus.BAD_REQUEST, "AUTH400", "Invalid user ID format."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "AUTH401", "Incorrect password."),
    MISSING_AUTHORITY(HttpStatus.FORBIDDEN, "AUTH403", "The token lacks authority information. Basic authority is required."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH404", "User not found."),
    ALREADY_EXIST_EMAIL(HttpStatus.CONFLICT, "AUTH409", "Email already registered."),
    PASSWORD_ALREADY_USED(HttpStatus.CONFLICT, "AUTH410", "Password already in use."),
    UNSUPPORTED_SOCIAL_TYPE(HttpStatus.NOT_FOUND, "AUTH415", "Unsupported social login type."),
    ILLEGAL_REGISTRATION_ID(HttpStatus.UNAUTHORIZED, "AUTH422", "Invalid registration ID."),
    REDIRECTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH500", "Redirection failed due to an internal server error."),

    // Social account linking errors
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH405", "Email not found."),
    SOCIAL_ACCOUNT_LINK_NOT_ALLOWED(HttpStatus.CONFLICT, "AUTH411", "Social account linking is not allowed."),
    ACCOUNT_ALREADY_LINKED_TO_ANOTHER_USER(HttpStatus.CONFLICT, "AUTH412", "This account is already linked to another user."),

    // Email-related errors
    EMAIL_VERIFICATION_SEND_FAILED(HttpStatus.BAD_REQUEST, "EMAIL400", "Failed to send email verification code."),

    // Token-related errors
    NULL_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN400", "The token is null."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN401", "The token is invalid."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN402", "The token has expired."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN403", "The refresh token is invalid or does not exist."),
    MISSING_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN404", "Token was not provided."),
    TOKEN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "TOKEN500", "An error occurred while processing the token."),

    // Cookie-related errors
    MISSING_COOKIES(HttpStatus.UNAUTHORIZED, "COOKIE404", "No cookies found. Please login via social login."),

    // Project-related errors
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT404", "The project does not exist."),

    // Character-related errors
    CHARACTER_NOT_FOUND(HttpStatus.NOT_FOUND, "CHARACTER404", "The character does not exist."),
    CHARACTER_NOT_IN_PROJECT(HttpStatus.BAD_REQUEST, "CHARACTER400", "The character does not belong to the project."),
    CHARACTERS_NOT_FOUND(HttpStatus.NOT_FOUND, "CHARACTER404", "Some characters do not exist in this project."),

    // Team member-related errors
    UNMATCHED_USER(HttpStatus.BAD_REQUEST, "MEMBER400", "User ID and email do not match."),
    ALREADY_REGISTERED_MEMBER(HttpStatus.CONFLICT, "MEMBER409", "The user is already added to the project."),

    // Page-related errors
    PAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "PAGE404", "The page does not exist."),
    START_PAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "PAGE404", "The start page from AI response does not exist in this project."),
    PAGES_NOT_FOUND(HttpStatus.NOT_FOUND, "PAGE404", "Some pages do not exist in this project."),

    // Test-related errors
    TEST_NOT_FOUND(HttpStatus.NOT_FOUND, "TEST404", "The test does not exist."),

    // Scenario-related errors
    SCENARIO_NOT_FOUND(HttpStatus.NOT_FOUND, "SCENARIO404", "The scenario does not exist."),
    SCENARIOS_NOT_FOUND(HttpStatus.NOT_FOUND, "SCENARIO404", "Some scenarios do not exist."),

    // Error-related errors
    ERROR_NOT_FOUND(HttpStatus.NOT_FOUND, "ERROR404", "The error does not exist."),

    WEBSOCKET_MESSAGE_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR500", "WebSocket 메시지 전송 실패"),
    JSON_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR500", "JSON 변환 오류"),
    WEBSOCKET_CONNECTION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR500", "WebSocket 연결 실패");

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
