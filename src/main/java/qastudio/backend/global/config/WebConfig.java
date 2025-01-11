package qastudio.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import qastudio.backend.global.handler.resolver.AuthResolver;

import java.util.Arrays;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthResolver authResolver;

    public WebConfig(AuthResolver authResolver) {
        this.authResolver = authResolver;
    }

    @Bean
    public static CorsConfigurationSource apiConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> allowedOriginPatterns = Arrays.asList("*");
        configuration.setAllowedOriginPatterns(allowedOriginPatterns);

        List<String> allowedHttpMethods = Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH");
        configuration.setAllowedMethods(allowedHttpMethods);

        List<String> allowedHeaders = Arrays.asList("*");
        configuration.setAllowedHeaders(allowedHeaders);

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authResolver);
    }
}
