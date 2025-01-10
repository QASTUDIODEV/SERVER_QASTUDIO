package qastudio.backend.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import qastudio.backend.jwt.JwtTokenFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 설정: JWT 기반 인증을 사용할 때는 비활성화 권장
                .csrf(csrf -> csrf.disable())

                // 인증 및 인가 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/css/**",
                                "/images/**",
                                "/js/**",
                                "/lib/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/v1/auth/sign-up",   // 회원가입 경로 허용
                                "/api/v1/auth/login/local", // 로컬 로그인 경로 허용
                                "/error",
                                "/favicon.ico",
                                "/default-ui.css",
                                "/health"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // 세션 관리: JWT 사용 시 세션 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))

                // 기본 로그인 폼 비활성화
                .formLogin(form -> form.disable())

                // HTTP 기본 인증 비활성화
                .httpBasic(httpBasic -> httpBasic.disable())

                // JWT 필터 추가: UsernamePasswordAuthenticationFilter 앞에 실행
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}