package qastudio.backend.global.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import qastudio.backend.global.apiPayload.code.exception.discord.DiscordClient;
import qastudio.backend.global.websocket.handler.CustomWebSocketExceptionHandler;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final DiscordClient discordClient;
    private final Environment environment;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new CustomWebSocketExceptionHandler(new TextWebSocketHandler(), discordClient, environment), "/connect")
                .setAllowedOrigins("*");
    }
}
