package qastudio.backend.global.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import qastudio.backend.domain.selenium.dto.response.PageResultResponse;
import qastudio.backend.global.apiPayload.code.exception.custom.WebSocketException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SeleniumWebSocketHandler extends TextWebSocketHandler {

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Boolean> stopExecutionFlags = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 직렬화

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket 연결됨: {}", session.getId());
        sessions.put(session.getId(), session);
        stopExecutionFlags.put(session.getId(), false);
        sendSessionId(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("WebSocket 연결 종료: {}", session.getId());
        sessions.remove(session.getId());
        stopExecutionFlags.remove(session.getId());
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        log.info("WebSocket 메시지 수신: {}", payload);

        if ("STOP".equalsIgnoreCase(payload.trim())) {
            log.info("실행 중지 요청 수신 - 세션 ID: {}", session.getId());
            stopExecutionFlags.put(session.getId(), true);
        }
    }

    public boolean shouldStopExecution(String sessionId) {
        return stopExecutionFlags.getOrDefault(sessionId, false);
    }
    public void sendHtmlAndCss(String sessionId, String html, String css, Long actionId) {
        WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                String jsonMessage = createExecutionResultResponse(html, css, actionId);
                session.sendMessage(new TextMessage(jsonMessage));
                log.info("WebSocket 메시지 전송 완료: {}", sessionId);
            } catch (IOException e) {
                log.error("❌ WebSocket 메시지 전송 실패: {}", e.getMessage());
                throw new WebSocketException(ErrorStatus.WEBSOCKET_MESSAGE_SEND_FAIL);
            }
        }
    }

    private String createExecutionResultResponse(String html, String css, Long actionId) {
        try {
            PageResultResponse response = new PageResultResponse("SUCCESS", List.of("실시간 HTML & CSS 업데이트"), html, css, actionId);
            return objectMapper.writeValueAsString(response);
        } catch (IOException e) {
            throw new WebSocketException(ErrorStatus.JSON_PROCESSING_ERROR);
        }
    }

    private void sendSessionId(WebSocketSession session) {
        if (session != null && session.isOpen()) {
            try {
                String jsonMessage = createSessionIdResponse(session.getId());
                session.sendMessage(new TextMessage(jsonMessage));
                log.info("WebSocket 세션 ID 전송 완료: {}", session.getId());
            } catch (IOException e) {
                log.error("❌ WebSocket 세션 ID 전송 실패: {}", e.getMessage());
                throw new WebSocketException(ErrorStatus.WEBSOCKET_MESSAGE_SEND_FAIL);
            }
        }
    }

    private String createSessionIdResponse(String sessionId) {
        try {
            return objectMapper.writeValueAsString(new SessionIdResponse(sessionId));
        } catch (IOException e) {
            throw new WebSocketException(ErrorStatus.JSON_PROCESSING_ERROR);
        }
    }

    private static class SessionIdResponse {
        private final String sessionId;

        public SessionIdResponse(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getSessionId() {
            return sessionId;
        }
    }

    public void sendImage(String sessionId, String base64Image) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "image");
        message.put("image", base64Image);

        sendMessage(sessionId, message);
    }
    private void sendMessage(String sessionId, Map<String, Object> message) {
        WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                String jsonMessage = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(jsonMessage));
                log.info("WebSocket 메시지 전송 성공: {}", sessionId);
            } catch (IOException e) {
                log.error("❌ WebSocket 메시지 전송 실패: {}", e.getMessage());
                throw new WebSocketException(ErrorStatus.WEBSOCKET_MESSAGE_SEND_FAIL);
            }
        } else {
            log.warn("WebSocket 세션을 찾을 수 없거나 닫혀 있음: {}", sessionId);
        }
    }

    public void sendImageBinaryWithMetadata(String sessionId, WebDriver driver) {
        WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                // 메타데이터(JSON) 전송 (이미지 ID, 설명)
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("type", "image"); // 식별자
                metadata.put("imageId", System.currentTimeMillis()); // 유니크한 ID
                metadata.put("description", "Selenium 캡처 이미지");

                String jsonMetadata = new ObjectMapper().writeValueAsString(metadata);
                session.sendMessage(new TextMessage(jsonMetadata)); // JSON 메타데이터 전송

                // 바이너리 데이터(스크린샷) 전송
                File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                BufferedImage bufferedImage = ImageIO.read(screenshotFile);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpg", outputStream); // PNG 대신 JPG 저장
                byte[] imageBytes = outputStream.toByteArray();

                session.sendMessage(new BinaryMessage(imageBytes)); // 📌 이미지 바이너리 전송
                log.info("WebSocket 바이너리 이미지 전송 완료: {}", sessionId);

            } catch (Exception e) {
                log.error("❌ WebSocket 바이너리 메시지 전송 실패: {}", e.getMessage());
            }
        }
    }

    public void closeSession(String sessionId) {
        WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                session.close();
                sessions.remove(sessionId);
                log.info("WebSocket session closed: {}", sessionId);
            } catch (Exception e) {
                log.error("Failed to close WebSocket session: {}", sessionId, e);
            }
        }
    }

}
