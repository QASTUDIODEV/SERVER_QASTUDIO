package qastudio.backend.global.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import qastudio.backend.domain.scenario.dto.response.ExecutionResultResponse;
import qastudio.backend.global.apiPayload.code.exception.custom.WebSocketException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SeleniumWebSocketHandler extends TextWebSocketHandler {

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 직렬화

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket 연결됨: {}", session.getId());
        sessions.put(session.getId(), session);
        sendSessionId(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("WebSocket 연결 종료: {}", session.getId());
        sessions.remove(session.getId());
    }

    public void sendHtmlAndCss(String sessionId, String html, String css) {
        WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                String jsonMessage = createExecutionResultResponse(html, css);
                session.sendMessage(new TextMessage(jsonMessage));
                log.info("WebSocket 메시지 전송 완료: {}", sessionId);
            } catch (IOException e) {
                log.error("❌ WebSocket 메시지 전송 실패: {}", e.getMessage());
                throw new WebSocketException(ErrorStatus.WEBSOCKET_MESSAGE_SEND_FAIL);
            }
        }
    }

    private String createExecutionResultResponse(String html, String css) {
        try {
            ExecutionResultResponse response = new ExecutionResultResponse("SUCCESS", List.of("실시간 HTML & CSS 업데이트"), html, css);
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
}
