package qastudio.backend.global.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import qastudio.backend.global.security.oauth.CustomOAuth2UserService;
import qastudio.backend.global.security.oauth.OAuth2SuccessHandler;
import qastudio.backend.global.security.jwt.JwtTokenFilter;

import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/css/**",
                                "/images/**",
                                "/js/**",
                                "/lib/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/v0/auth/sign-up", // 회원가입 제외
                                "/api/v0/auth/sign-up/email", // 이메일 인증 제외
                                "/api/v0/auth/login", // 로그인 제외
                                "/api/v0/auth/update/password", // 비밀번호 변경 제외
                                "/api/v0/auth/check", // 토큰 확인 제외
                                "/error",
                                "/favicon.ico",
                                "/default-ui.css",
                                "/health"
                        ).permitAll()
                        .requestMatchers("/oauth2/authorization/**").access((authentication, context) -> {
                            // HttpServletRequest를 직접 가져오는 대신 SecurityContext를 활용
                            boolean skipAuth = context.getRequest().getParameter("skipAuth") != null
                                    && "true".equals(context.getRequest().getParameter("skipAuth"));

                            // SecurityContext에서 인증 정보 가져오기
                            Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();

                            // 인증된 사용자이거나 skipAuth 파라미터가 true이면 허용
                            boolean allowAccess = skipAuth || (authentication1 != null && authentication1.isAuthenticated());
                            return new AuthorizationDecision(allowAccess);
                        })
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .oauth2Login(oauth ->
                        oauth.userInfoEndpoint(c -> c.userService(customOAuth2UserService))
                                .successHandler(oAuth2SuccessHandler)
                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(
                List.of(
                        "http://localhost:8080",
                        "http://localhost:3000",
                        "https://localhost:5173",
                        "https://www.qa-studio.com",
                        "https://back.qa-studio.com"
                )
        );
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Set-Cookie", "Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}