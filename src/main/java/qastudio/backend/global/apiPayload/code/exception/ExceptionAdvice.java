package qastudio.backend.global.apiPayload.code.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import qastudio.backend.global.apiPayload.ApiResponse;
import qastudio.backend.global.apiPayload.code.ErrorReasonDTO;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleValidationException(ConstraintViolationException e, WebRequest request) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(constraintViolation -> constraintViolation.getMessage())
                .findFirst()
                .orElse("ConstraintViolationException 발생 중 오류");

        return createErrorResponse(ErrorStatus._BAD_REQUEST, errorMessage, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers, org.springframework.http.HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();

        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            String fieldName = fieldError.getField();
            String errorMessage = Optional.ofNullable(fieldError.getDefaultMessage()).orElse("");
            errors.merge(fieldName, errorMessage, (existing, newMessage) -> existing + ", " + newMessage);
        });

        return createErrorResponse(ErrorStatus._BAD_REQUEST, errors, request);
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<Object> handleGeneralException(GeneralException e, HttpServletRequest request) {
        ErrorReasonDTO errorReason = e.getErrorReasonHttpStatus();
        return createErrorResponse(errorReason, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleOtherExceptions(Exception e, WebRequest request) {
        log.error("Unhandled exception occurred: ", e);
        return createErrorResponse(ErrorStatus._INTERNAL_SERVER_ERROR, e.getMessage(), request);
    }

    private ResponseEntity<Object> createErrorResponse(ErrorReasonDTO errorReason, HttpServletRequest request) {
        ApiResponse<Object> body = ApiResponse.onFailure(errorReason.getCode(), errorReason.getMessage(), null);
        WebRequest webRequest = new ServletWebRequest(request);
        return ResponseEntity.status(errorReason.getHttpStatus()).body(body);
    }

    private ResponseEntity<Object> createErrorResponse(ErrorStatus errorStatus, Object result, WebRequest request) {
        ApiResponse<Object> body = ApiResponse.onFailure(errorStatus.getCode(), errorStatus.getMessage(), result);
        return ResponseEntity.status(errorStatus.getHttpStatus()).body(body);
    }
}