package qastudio.backend.global.websocket.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator;
import qastudio.backend.global.apiPayload.code.exception.custom.ChatException;
import qastudio.backend.global.apiPayload.code.exception.discord.DiscordClient;
import qastudio.backend.global.apiPayload.code.exception.discord.DiscordMessage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class CustomWebSocketExceptionHandler extends ExceptionWebSocketHandlerDecorator {

    private final DiscordClient discordClient;
    private final Environment environment;

    public CustomWebSocketExceptionHandler(WebSocketHandler delegate, DiscordClient discordClient, Environment environment) {
        super(delegate);
        this.discordClient = discordClient;
        this.environment = environment;
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        try {
            try {
                getDelegate().handleMessage(session, message);
            } catch (ChatException ex) {
                // 400 에러 설정
                log.info("채팅 관련 에러");
                String errorMessage = String.format("{\"isSuccess\": \"false\", \"code\": \"%s\", \"message\": \"%s\"}", ex.getCode(), ex.getErrorReason().getMessage());
                session.sendMessage(new TextMessage(errorMessage));
            } catch (Exception e) {
                log.info("나머지 에러");
                e.printStackTrace();
                String errorMessage = "{\"isSuccess\": \"false\", \"code\": \"500\", \"message\": \"예외적 오류 서버 로그를 확인 해보 세요.\"}";
                session.sendMessage(new TextMessage(errorMessage));
                if (!Arrays.asList(environment.getActiveProfiles()).contains("local")) {
                    discordClient.sendAlarm(createMessage(e));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.handleMessage(session, message);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        // connection 후 닫기
    }

    private DiscordMessage createMessage(Exception e) {
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
                                                        + "소켓 관련 에러"
                                                        + "\n"
                                                        + "### 📄 Stack Trace\n"
                                                        + "```\n"
                                                        + getStackTrace(e).substring(0, 1000)
                                                        + "\n```")
                                        .build()))
                .build();
    }

    private String getStackTrace(Exception e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}