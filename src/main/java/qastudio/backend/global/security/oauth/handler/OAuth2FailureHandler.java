package qastudio.backend.global.security.oauth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    private static final String REDIRECT_URL = "https://localhost:5173/login/success";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String errorMessage = (exception.getMessage() != null) ? exception.getMessage() : "This social account cannot be linked.";

        response.setStatus(HttpServletResponse.SC_OK);

        String redirectUrl = REDIRECT_URL + "?status=error&message=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        response.sendRedirect(redirectUrl);
    }
}