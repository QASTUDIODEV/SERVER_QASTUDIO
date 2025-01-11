package qastudio.backend.global.apiPayload.code.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.env.Environment;
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
import qastudio.backend.global.apiPayload.code.exception.discord.DiscordClient;
import qastudio.backend.global.apiPayload.code.exception.discord.DiscordMessage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
@RequiredArgsConstructor
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    private final DiscordClient discordClient;
    private final Environment environment;

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

        if (Arrays.asList(environment.getActiveProfiles()).contains("develop") || Arrays.asList(environment.getActiveProfiles()).contains("prod")) {
            sendDiscordAlarm(e, request);
        }

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

    private void sendDiscordAlarm(Exception e, WebRequest request) {
        discordClient.sendAlarm(createMessage(e, request));
    }

    private DiscordMessage createMessage(Exception e, WebRequest request) {
        return DiscordMessage.builder()
                .content("# 🚨 에러 발생 비이이이이사아아아앙")
                .embeds(
                        List.of(
                                DiscordMessage.Embed.builder()
                                        .title("ℹ️ 에러 정보")
                                        .description(
                                                "### 🕖 발생 시간\n"
                                                        + LocalDateTime.now()
                                                        + "\n"
                                                        + "### 🔗 요청 URL\n"
                                                        + createRequestFullPath(request)
                                                        + "\n"
                                                        + "### 📄 Stack Trace\n"
                                                        + "```\n"
                                                        + getStackTrace(e).substring(0, 1000)
                                                        + "\n```")
                                        .build()))
                .build();
    }

    private String createRequestFullPath(WebRequest webRequest) {
        HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
        String fullPath = request.getMethod() + " " + request.getRequestURL();

        String queryString = request.getQueryString();
        if (queryString != null) {
            fullPath += "?" + queryString;
        }

        return fullPath;
    }

    private String getStackTrace(Exception e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}